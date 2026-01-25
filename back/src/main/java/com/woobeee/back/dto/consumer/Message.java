package com.woobeee.back.dto.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.util.List;

/**
 * model-trigger.dm.request
 * model-trigger.mlops.response
 */
@Builder
public record Message<T>(
        Header header,
        T data
) {
    @Builder
    public record Header(
            Boolean isSuccessful,
            String message,
            String from,
            List<String> to
    ) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private Boolean isSuccessful;
            private String message;
            private String from;
            private List<String> to;

            public Builder isSuccessful(Boolean isSuccessful) {
                this.isSuccessful = isSuccessful;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder from(String from) {
                this.from = from;
                return this;
            }

            public Builder to(List<String> to) {
                this.to = to;
                return this;
            }

            public Header build() {
                return new Header(isSuccessful, message, from, to);
            }
        }
    }

    public record Body<T>(
            T data
    ) {}

    public static String signInAfterMessage(JsonNode jsonNode, ObjectMapper objectMapper) {
        try {
            Message<JsonNode> message = new Message<>(
                    Header.builder()
                            .isSuccessful(true)
                            .message("sign-in-after-message")
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