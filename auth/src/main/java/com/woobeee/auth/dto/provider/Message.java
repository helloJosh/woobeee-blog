package com.woobeee.auth.dto.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * model-trigger.dm.request
 * model-trigger.mlops.response
 * @param <T>
 */
@Data
@NoArgsConstructor
public class Message<T> {
    private Header header;
    private T data;

    public Message(Header header, T data) {
        this.header = header;
        this.data = data;
    }

    public Message(Header header) {
        this.header = header;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private Boolean isSuccessful;
        private String message;
        private String from;
        private List<String> to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body<T> {
        private T data;
    }

    public static String signInAfterMessage(JsonNode jsonNode, ObjectMapper objectMapper) {
        try {
            Message<JsonNode> message = new Message<>(
                    Header.builder()
                            .isSuccessful(true)
                            .message("signIn after message")
                            .from("auth")
                            .to(List.of("back"))
                            .build(),
                    jsonNode
            );
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize signIn after message", e);
        }
    }

}