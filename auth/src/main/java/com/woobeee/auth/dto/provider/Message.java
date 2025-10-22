package com.woobeee.auth.dto.provider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * model-trigger.dm.request
 * model-trigger.mlops.response
 * @param <T>
 */
public record Message<T>(
        Header header,
        T data
) {

    public record Header(
            Boolean isSuccessful,
            String message,
            String from,
            List<String> to
    ) {}

    public record Body<T>(
            T data
    ) {}

    public static String signInAfterMessage(JsonNode jsonNode, ObjectMapper objectMapper) {
        try {
            Message<JsonNode> message = new Message<>(
                    new Header(
                            true,
                            "sign-in-after-message",
                            "auth",
                            List.of("back")
                    ),
                    jsonNode
            );
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize sign-in-after-message", e);
        }
    }
}