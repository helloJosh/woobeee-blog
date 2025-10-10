package com.woobeee.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.chat.dto.ChatRequest;
import com.woobeee.chat.dto.PostExportDto;
import com.woobeee.chat.importer.RagImporter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final WebClient vllm;
    private final RagImporter importer;
    private final ObjectMapper om;

    private static final int MAX_TITLE = 500;
    private static final int MAX_TEXT  = 10_000;
    private static final int MAX_URL   = 512;
    private static final int MAX_CAT   = 256;

    /**
     * Rag 인자 포함 요청
     */
    public Flux<String> streamFromVllmWithRag(ChatRequest req) {
        // 1️⃣ RAG 문서 로드 (MinIO → post.json 파싱)
        return importer.load()
                .flatMapMany(posts -> {
                    String ragContext = buildRagContext(posts);

                    var payload = om.createObjectNode();
                    payload.put("model", "unsloth/Qwen3-4B-Instruct-2507");

                    var messages = om.createArrayNode();

                    // RAG context를 system 메시지로 주입
                    var sys = om.createObjectNode();
                    sys.put("role", "system");
                    sys.put("content", ragContext);
                    messages.add(sys);

                    // 사용자의 기존 대화 메시지 추가
                    req.messages().forEach(m -> {
                        var n = om.createObjectNode();
                        n.put("role", m.role());
                        n.put("content", m.content());
                        messages.add(n);
                    });

                    payload.set("messages", messages);
                    payload.put("stream", true);
                    if (req.maxTokens() != null) payload.put("max_tokens", req.maxTokens());

                    // 4️⃣ vLLM으로 요청 스트림 전송
                    return vllm.post()
                            .uri("/v1/chat/completions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .bodyValue(payload)
                            .retrieve()
                            .bodyToFlux(DataBuffer.class)
                            .map(this::dbToString)
                            .scan(new StringBuilder(), (acc, chunk) -> { acc.append(chunk); return acc; })
                            .flatMap(sb -> {
                                List<String> events = new ArrayList<>();
                                int idx;
                                while ((idx = indexOfDoubleNewline(sb)) != -1) {
                                    String evt = sb.substring(0, idx).trim();
                                    sb.delete(0, idx + 2);
                                    if (!evt.isEmpty()) events.add(evt);
                                }
                                return Flux.fromIterable(events);
                            })
                            .filter(line -> line.startsWith("data:"))
                            .map(line -> line.substring("data:".length()).trim())
                            .takeUntil("[DONE]"::equals)
                            .filter(data -> !"[DONE]".equals(data))
                            .map(this::extractDeltaContent)
                            .filter(s -> !s.isBlank());
                });
    }

    /**
     * Rag 인자없이 요청
     */
    public Flux<String> streamFromVllm(ChatRequest req) {
        // vLLM OpenAI 호환 /v1/chat/completions 로 POST + stream: true
        var payload = om.createObjectNode();
        payload.put("model", "unsloth/Qwen3-4B-Instruct-2507");
        var messages = om.createArrayNode();
        req.messages().forEach(m -> {
            var n = om.createObjectNode();
            n.put("role", m.role());
            n.put("content", m.content());
            messages.add(n);
        });
        payload.set("messages", messages);
        payload.put("stream", true);
        if (req.maxTokens() != null) payload.put("max_tokens", req.maxTokens());

        return vllm.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(payload)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(this::dbToString)                     // DataBuffer → String
                // 아래부터는 SSE("data: ...\n\n") 청크를 안전하게 파싱
                .scan(new StringBuilder(), (acc, chunk) -> { acc.append(chunk); return acc; })
                .flatMap(sb -> {
                    List<String> events = new ArrayList<>();
                    int idx;
                    while ((idx = indexOfDoubleNewline(sb)) != -1) {
                        String evt = sb.substring(0, idx).trim();
                        sb.delete(0, idx + 2); // "\n\n" 건너뛰기
                        if (!evt.isEmpty()) events.add(evt);
                    }
                    return Flux.fromIterable(events);
                })
                .filter(line -> line.startsWith("data:"))
                .map(line -> line.substring("data:".length()).trim())
                .takeUntil("[DONE]"::equals)
                .filter(data -> !"[DONE]".equals(data))
                .map(this::extractDeltaContent)            // JSON에서 choices[0].delta.content만 추출
                .filter(s -> !s.isBlank());
    }

    private String buildRagContext(List<PostExportDto> posts) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a knowledgeable assistant. Use the following blog posts from woobeee.com to answer the user's question.\n\n");

        for (int i = 0; i < posts.size(); i++) {
            PostExportDto p = posts.get(i);
            sb.append("[").append(i + 1).append("]\n")
                    .append("titleKo: ").append(p.titleKo()).append("\n")
                    .append("textKo: ").append(p.textKo()).append("\n")
                    .append("titleEn: ").append(p.titleEn()).append("\n")
                    .append("textKr: ").append(p.textEn()).append("\n")
                    .append("url: ").append("https://woobeee.com/post/").append(p.id()).append("\n")
                    .append("category: ").append(p.categoryNameKo()).append("\n\n");
        }

        sb.append("Answer concisely and cite the relevant post numbers and url like \n\n [1] https://woobeee.com/post/1 \n, [2] https://woobeee.com/post/2 \n.");
        return sb.toString();
    }

    private String dbToString(DataBuffer db) {
        try {
            byte[] bytes = new byte[db.readableByteCount()];
            db.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } finally {
            DataBufferUtils.release(db);
        }
    }

    private int indexOfDoubleNewline(CharSequence sb) {
        for (int i = 0; i < sb.length() - 1; i++) {
            char c1 = sb.charAt(i);
            char c2 = sb.charAt(i + 1);
            // \n\n 또는 \r\n\r\n 를 모두 허용
            if (c1 == '\n' && c2 == '\n') return i;
            if (i + 3 < sb.length()
                    && sb.charAt(i) == '\r' && sb.charAt(i + 1) == '\n'
                    && sb.charAt(i + 2) == '\r' && sb.charAt(i + 3) == '\n') return i + 1;
        }
        return -1;
    }

    private String extractDeltaContent(String dataJson) {
        try {
            JsonNode root = om.readTree(dataJson);
            var delta = root.at("/choices/0/delta/content");
            return delta.isTextual() ? delta.asText() : "";
        } catch (Exception e) {
            return "";
        }
    }
}