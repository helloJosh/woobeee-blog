package com.woobeee.back.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.back.aop.Idempotent;
import com.woobeee.back.dto.IdempotencyResult;
import com.woobeee.back.dto.consumer.Message;
import com.woobeee.back.service.IdempotencyService;
import com.woobeee.back.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInConsumer {
    private final ObjectMapper objectMapper;
    private final UserInfoService userInfoService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = "${spring.cloud.config.profile}-sign-in-trigger",
            groupId = "${spring.cloud.config.profile}-${spring.kafka.consumer.group-id}"
    )
    public void listenExtract(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) throws Exception {
        String eventId = key;
        String payloadHash = DigestUtils.md5DigestAsHex(payload.getBytes(StandardCharsets.UTF_8));

        IdempotencyResult r = idempotencyService.begin(
                "auth-signin-consumer-event",  // 고정
                eventId,                 // domainKey = eventId
                payloadHash
        );

        Message<JsonNode> parsed = objectMapper.readValue(
                payload, new TypeReference<Message<JsonNode>>() {}
        );

        JsonNode requestNode = parsed.data();
        String id = requestNode.get("id").asText();
        String loginId = requestNode.get("loginId").asText();

        try {
            userInfoService.signIn(id, loginId);
            idempotencyService.complete("auth-signin-consumer-event", eventId, 200, "OK");
        } catch (Exception e) {
            idempotencyService.fail("auth-signin-consumer-event", eventId, 500, e.getMessage());
            throw e;
        }
    }
}
