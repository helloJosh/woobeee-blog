package com.woobeee.blog.member.dto;

import lombok.Builder;


/**
 * 로그인 응답 레코드.
 *
 * @param loginId 유저 로그인 아이디
 * @param password 유저 패스워드
 */
@Builder
public record LoginResponse(
        String loginId,
        String password
) {
}
