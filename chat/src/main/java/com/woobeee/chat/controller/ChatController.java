package com.woobeee.chat.controller;

import com.woobeee.chat.dto.ChatRequest;
import com.woobeee.chat.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService svc;

    public ChatController(ChatService svc) {
        this.svc = svc;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@RequestBody ChatRequest request) {
        return svc.streamFromVllm(request)
                .map(token -> ServerSentEvent.builder(token).event("chunk").build());
    }
}