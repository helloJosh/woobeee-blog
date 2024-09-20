package com.woobeee.blog.member.dto;

import com.woobeee.blog.member.entity.enums.Auth;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


/**
 * 회원가입 요청 레코드.
 *
 * @param loginId 로그인아이디
 * @param password 비밀번호
 * @param name 회원이름
 * @param email 회원이메일
 * @param status 회원상태
 * @param auth 회원권한
 */
@Builder
public record MemberRequest(
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank Status status,
        @NotBlank Auth auth
        ) {
}
