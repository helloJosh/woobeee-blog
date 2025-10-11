package com.woobeee.chat.controller;

import com.woobeee.chat.dto.ChatRequest;
import com.woobeee.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService svc;

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<ServerSentEvent<String>> stream(@RequestBody ChatRequest request) {
        log.info("{} chat request", request);

        return svc.streamFromVllmWithRag(request)
                .doOnNext(token -> log.info("SSE token: {}", token)) // ✅ 각 토큰 출력
                .map(token -> ServerSentEvent.builder(token)
                        .event("chunk")
                        .build())
                .doOnComplete(() -> log.info("✅ SSE Stream completed"))
                .doOnError(e -> log.error("❌ SSE Stream error", e));
    }
}