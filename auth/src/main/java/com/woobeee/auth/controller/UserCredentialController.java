package com.woobeee.auth.controller;


import com.woobeee.auth.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@Tag(name = "User Credential Controller", description = "유저 인증 컨트롤러")
public interface UserCredentialController {
    @Operation(
            summary = "로그인 API"
    )
    @GetMapping("/login")
    ApiResponse<Void> login();

    @Operation(
            summary = "로그아웃 API"
    )
    @GetMapping("/logout")
    ApiResponse<Void> logout();


    @Operation(
            summary = "로그아웃 API"
    )
    @PostMapping("/signIn")
    ApiResponse<Void> signIn();

}
