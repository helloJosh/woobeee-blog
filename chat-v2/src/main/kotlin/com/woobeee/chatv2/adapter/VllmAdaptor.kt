package com.woobeee.chatv2.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.woobeee.chatv2.dto.ChatMessageDto
import com.woobeee.chatv2.dto.ChatRequest
import com.woobeee.chatv2.dto.PostExportDto
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class VllmAdaptor (
    private val vllmWebClient: WebClient,
    private val importer: RagImporter,
    private val objectMapper: ObjectMapper
){
    companion object {
        private const val MODEL_NAME = "unsloth/Qwen3-4B-Instruct-2507"
    }

    fun askOnceWithRag(req: ChatRequest): Mono<String> {
        return importer.load() // RAG 문서 로드
            .flatMap { posts ->
                val ragContext = buildRagContext(posts)

                val payload = objectMapper.createObjectNode().apply {
                    put("model", MODEL_NAME)

                    val messagesNode = objectMapper.createArrayNode()

                    // RAG context 추가
                    val sys = objectMapper.createObjectNode().apply {
                        put("role", "system")
                        put("content", ragContext)
                    }
                    messagesNode.add(sys)

                    // TODO : 사용
//                    req.messages.forEach { m ->
//                        val node = objectMapper.createObjectNode().apply {
//                            put("role", m.role)
//                            put("content", m.content)
//                        }
//                        messagesNode.add(node)
//                    }
//
//                    set("messages", messagesNode)
                    req.maxTokens?.let { put("max_tokens", it) }
                }

                vllmWebClient.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .map { extractFinalContent(it) }
            }
    }

    private fun buildRagContext(posts: List<PostExportDto>): String {
        return posts.joinToString("\n\n") { p ->
            val title = listOfNotNull(p.titleKo, p.titleEn).joinToString(" / ").take(500)
            val text = listOfNotNull(p.textKo, p.textEn).joinToString("\n").take(10_000)
            "title: $title\n$text"
        }
    }

    /** vLLM 응답에서 최종 content만 파싱 */
    private fun extractFinalContent(json: String): String {
        return try {
            val root = objectMapper.readTree(json)
            val choices = root.path("choices")
            if (choices.isArray && choices.size() > 0)
                choices[0].path("message").path("content").asText("")
            else
                ""
        } catch (e: Exception) {
            ""
        }
    }
}