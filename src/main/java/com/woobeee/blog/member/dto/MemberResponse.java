package com.woobeee.blog.member.dto;

import com.woobeee.blog.member.entity.enums.Auth;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


/**
 * 회원 조회 레코드.
 *
 * @param id 맴버아이디
 * @param loginId 로그인아이디
 * @param password 패스워드
 * @param name 이름
 * @param email 이메일
 * @param status 회원상태
 * @param auth 회원권한
 */
@Builder
public record MemberResponse(
        Long id,
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank Status status,
        @NotBlank Auth auth) {
}
