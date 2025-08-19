package com.woobeee.auth.controller;

import com.woobeee.auth.support.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class UserCredentialControllerImpl implements UserCredentialController{
    private final KafkaTemplate template;
    /// GET /api/auth/login
    @Override
    public ApiResponse<Void> login() {
        log.info("login request");
        template.send("test-topic", "hello");
        return ApiResponse.success("login request success");
    }

    /// GET /api/auth/logout
    @Override
    public ApiResponse<Void> logout() {
        log.info("logout request");
        return ApiResponse.success("logout success");
    }

    /// POST /api/auth/signIn
    @Override
    public ApiResponse<Void> signIn() {
        log.info("sign in request");
        return ApiResponse.success("sign in success");
    }

    @KafkaListener(topics = "test-topic")
    public void consume(String message) {
        System.out.println("Received: " + message);
    }
}
