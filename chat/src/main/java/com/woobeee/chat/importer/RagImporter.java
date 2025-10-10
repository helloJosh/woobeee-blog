package com.woobeee.chat.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.chat.config.MinioConfig;
import com.woobeee.chat.dto.PostExportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;

@Component
@RequiredArgsConstructor
public class RagImporter {
    private final S3Client s3;
    private final MinioConfig.MinioProperties minio;
    private final ObjectMapper om;

    private final AtomicReference<List<PostExportDto>> cache = new AtomicReference<>(List.of());
    private volatile long lastLoadMs = 0L;
    private static final long TTL_MS = java.time.Duration.ofMinutes(59).toMillis();

    public Mono<List<PostExportDto>> load() {
        return loadAll();
    }

    public Flux<PostExportDto> search(String query, int topK) {
        return loadAll()
                .flatMapMany(all -> {
                    var q = tokenize(query);
                    var scored = all.stream()
                            .map(p -> new Scored(p, score(p, q)))
                            .sorted(comparingDouble((Scored s) -> s.score).reversed())
                            .limit(Math.max(1, topK))
                            .map(Scored::p)
                            .toList();
                    return Flux.fromIterable(scored);
                });
    }

    private Mono<List<PostExportDto>> loadAll() {
        long now = System.currentTimeMillis();
        if (!cache.get().isEmpty() && (now - lastLoadMs) < TTL_MS) {
            return Mono.just(cache.get());
        }
        return Mono.fromCallable(() -> {
                    var get = GetObjectRequest.builder()
                            .bucket(minio.getBucket())
                            .key("post.json")
                            .build();
                    try (ResponseInputStream<?> in = s3.getObject(get)) {
                        byte[] bytes = in.readAllBytes();
                        List<PostExportDto> list = om.readValue(bytes, new TypeReference<>() {});
                        cache.set(list);
                        lastLoadMs = System.currentTimeMillis();
                        return list;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private record Scored(PostExportDto p, double score) {}
    private static Set<String> tokenize(String q) {
        if (q == null) return java.util.Set.of();
        return Arrays
                .stream(q.toLowerCase(java.util.Locale.ROOT)
                        .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                        .split("\\s+"))
                .filter(t -> t.length() > 1)
                .collect(Collectors.toSet());
    }
    private static double score(PostExportDto p, Set<String> q) {
        if (q.isEmpty()) return 1e-6; // 쿼리 없으면 최소 점수
        String hay = String.join(" ",
                        nz(p.titleKo()), nz(p.titleEn()),
                        nz(p.textKo()),  nz(p.textEn()),
                        nz(p.categoryNameKo()), nz(p.categoryNameEn()))
                .toLowerCase(java.util.Locale.ROOT);
        int hits = 0;
        for (String token : q) if (hay.contains(token)) hits++;
        return hits / (double) q.size();
    }
    private static String nz(String s) { return s == null ? "" : s; }
}
