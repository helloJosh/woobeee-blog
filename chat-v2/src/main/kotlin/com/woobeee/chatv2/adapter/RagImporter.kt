package com.woobeee.chatv2.adapter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.woobeee.chatv2.config.MinioConfig
import com.woobeee.chatv2.dto.PostExportDto
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

@Component
class RagImporter(
    private val s3: S3Client,
    private val minio: MinioConfig.MinioProperties,
    private val om: ObjectMapper
) {
    // 캐시된 포스트들
    private val cache = AtomicReference<List<PostExportDto>>(emptyList())

    // 마지막으로 로드한 시각
    @Volatile
    private var lastLoadMs: Long = 0L

    // 59분 TTL
    private val ttlMs: Long = Duration.ofMinutes(59).toMillis()

    /**
     * 그냥 전체 로드
     */
    fun load(): Mono<List<PostExportDto>> =
        loadAll()

    /**
     * 간단 토큰 스코어링으로 topK 검색
     */
    fun search(query: String?, topK: Int): Flux<PostExportDto> {
        return loadAll()
            .flatMapMany { all ->
                val q = tokenize(query)
                val scored = all
                    .map { p -> Scored(p, score(p, q)) }
                    .sortedByDescending { it.score }
                    .take(maxOf(1, topK))
                    .map { it.p }
                Flux.fromIterable(scored)
            }
    }

    /**
     * S3 (MinIO)에서 post.json 읽어서 캐시하는 부분
     */
    private fun loadAll(): Mono<List<PostExportDto>> {
        val now = System.currentTimeMillis()

        // 캐시가 있고 TTL 안 지났으면 캐시 반환
        if (cache.get().isNotEmpty() && (now - lastLoadMs) < ttlMs) {
            return Mono.just(cache.get())
        }

        return Mono.fromCallable {
            val get = GetObjectRequest.builder()
                .bucket(minio.bucket)
                .key("post.json")
                .build()

            s3.getObject(get).use { input: ResponseInputStream<*> ->
                val bytes = input.readAllBytes()
                val list: List<PostExportDto> =
                    om.readValue(bytes, object : TypeReference<List<PostExportDto>>() {})
                cache.set(list)
                lastLoadMs = System.currentTimeMillis()
                list
            }
        }.subscribeOn(Schedulers.boundedElastic())
    }

    // --------- 아래는 Java 코드 그대로 코틀린화한 헬퍼들 ---------

    private data class Scored(
        val p: PostExportDto,
        val score: Double
    )

    private fun tokenize(q: String?): Set<String> {
        if (q == null) return emptySet()
        return q.lowercase()
            .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 }
            .toSet()
    }

    private fun score(p: PostExportDto, q: Set<String>): Double {
        if (q.isEmpty()) return 1e-6 // 쿼리 없으면 최소 점수

        val hay = listOf(
            nz(p.titleKo), nz(p.titleEn),
            nz(p.textKo), nz(p.textEn),
            nz(p.categoryNameKo), nz(p.categoryNameEn)
        )
            .joinToString(" ")
            .lowercase()

        var hits = 0
        for (token in q) {
            if (hay.contains(token)) hits++
        }
        return hits.toDouble() / q.size.toDouble()
    }

    private fun nz(s: String?): String = s ?: ""
}