package com.woobeee.auth.controller;

import com.woobeee.auth.dto.request.OauthTokenRequest;
import com.woobeee.auth.dto.response.ApiResponse;
import com.woobeee.auth.service.OauthUserCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class UserCredentialControllerImpl implements UserCredentialController{
    private final OauthUserCredentialService oauthUserCredentialService;
    /// GET /api/auth/login
    @Override
    public ApiResponse<String> login(OauthTokenRequest request) {
        log.info("login request");
        return ApiResponse.success(
                oauthUserCredentialService.logIn(request.getIdToken()),
                "login request success"
        );
    }

    /// POST /api/auth/signIn
    @Override
    public ApiResponse<String> signIn(OauthTokenRequest request) {
        log.info("sign in request");
        return ApiResponse.success(
                oauthUserCredentialService.signIn(request.getIdToken()),
                "sign in success");
    }

    /// GET /api/auth/logout
    @Override
    public ApiResponse<Void> logout() {
        log.info("logout request");
        return ApiResponse.success("logout success");
    }


}
