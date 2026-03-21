package com.woobeee.auth.controller;

import com.woobeee.auth.dto.request.OauthTokenRequest;
import com.woobeee.auth.dto.request.PostOauthSignUpRequest;
import com.woobeee.auth.dto.response.ApiResponse;
import com.woobeee.auth.dto.response.AuthTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@Tag(name = "User Credential Controller", description = "유저 인증 컨트롤러")
public interface UserCredentialController {
    @Operation(
            summary = "로그인 API"
    )
    @PostMapping("/login")
    ApiResponse<AuthTokenResponse> login(
            @RequestBody OauthTokenRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "로그아웃 API"
    )
    @PostMapping("/signIn")
    ApiResponse<AuthTokenResponse> signIn(
            @RequestBody OauthTokenRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "회원가입 API"
    )
    @PostMapping("/signUp")
    ApiResponse<AuthTokenResponse> signUp(
            @RequestBody PostOauthSignUpRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "토큰 재발급 API"
    )
    @PostMapping("/refresh")
    ApiResponse<AuthTokenResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    );

    @Operation(
            summary = "로그아웃 API"
    )
    @GetMapping("/logout")
    ApiResponse<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    );
}
