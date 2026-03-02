package com.woobeee.back.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.back.dto.IdempotencyResult;
import com.woobeee.back.dto.request.PostSignUpRequest;
import com.woobeee.back.service.IdempotencyService;
import com.woobeee.back.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignUpConsumer {
    private final ObjectMapper objectMapper;
    private final UserInfoService userInfoService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = "${spring.cloud.config.profile:${spring.config.activate.on-profile:dev}}-sign-up-trigger",
            groupId = "${spring.cloud.config.profile:${spring.config.activate.on-profile:dev}}-${spring.kafka.consumer.group-id}"
    )
    public void listenExtract(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) throws Exception {
        log.info("sign-up message received. key={}", key);
        String eventId = key;
        String payloadHash = DigestUtils.md5DigestAsHex(payload.getBytes(StandardCharsets.UTF_8));

        IdempotencyResult r = idempotencyService.begin(
                "auth-signup-consumer-event",
                eventId,
                payloadHash
        );

        PostSignUpRequest request = objectMapper.readValue(payload, PostSignUpRequest.class);

        try {
            userInfoService.signUp(request);
            idempotencyService.complete("auth-signup-consumer-event", eventId, 200, "OK");
        } catch (Exception e) {
            idempotencyService.fail("auth-signup-consumer-event", eventId, 500, e.getMessage());
            throw e;
        }
    }
}
