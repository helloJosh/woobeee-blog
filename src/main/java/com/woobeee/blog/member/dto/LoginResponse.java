package com.woobeee.blog.member.dto;

import lombok.Builder;

/**
 * 로그인 응답 레코드.
 *
 * @param accessToken 액서스 토큰
 * @param refreshToken 리프레쉬 토큰
 */
@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken) {
}
