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
    private final UserInfoService userInfoService;

    @KafkaListener(
            topics = "${spring.cloud.config.profile}-sign-in-trigger",
            groupId = "${spring.cloud.config.profile}-${spring.kafka.consumer.group-id}"
    )
    public void listenExtract(String message) throws Exception {

        Message<JsonNode> parsed = objectMapper.readValue(
                message, new TypeReference<Message<JsonNode>>() {}
        );

        JsonNode requestNode = parsed.data();
        String id = requestNode.get("id").asText();
        String loginId = requestNode.get("loginId").asText();

        userInfoService.signIn(id, loginId);
    }
}
