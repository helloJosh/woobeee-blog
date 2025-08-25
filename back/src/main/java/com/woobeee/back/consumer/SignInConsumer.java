package com.woobeee.back.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.back.dto.consumer.Message;
import com.woobeee.back.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final UserInfoService userInfoService;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @KafkaListener(
            topics = "${spring.cloud.config.profile}-sign-in-trigger",
            groupId = "${spring.cloud.config.profile}-${spring.kafka.consumer.group-id}")
    public void listenExtract(String message) {
        log.info(message);
        JsonNode requestNode = null;
        try {

            Message<JsonNode> parse = objectMapper.readValue(message,
                    new TypeReference<Message<JsonNode>>() {});

            requestNode = parse.getData();
            String id = requestNode.get("id").asText();
            String loginId = requestNode.get("loginId").asText();

            userInfoService.signIn(id, loginId);

        } catch (IOException e) {
            kafkaTemplate.send(
                    profile + "-signin-trigger-error",
                    KafkaHeaders.RECEIVED_KEY,
                    null
            );

            log.error(message, e);
        }
    }
}
