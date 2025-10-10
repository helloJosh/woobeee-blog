package com.woobeee.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.chat.dto.ChatRequest;
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
    private final ObjectMapper om;

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