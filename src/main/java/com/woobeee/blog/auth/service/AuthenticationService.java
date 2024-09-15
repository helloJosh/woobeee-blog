package com.woobeee.blog.auth.service;

import com.woobeee.blog.auth.exception.AuthenticationInvalidException;

import java.util.Map;

/**
 * 인증 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface AuthenticationService {
    /**
     * 맴버 로그인.
     *
     * @param loginId 로그인아이디
     * @param password 비밀번호
     * @return "AccessToken", "RefreshToken" Map
     * @throws AuthenticationInvalidException 인증오류에러
     */
    Map<String, String> login(String loginId, String password) throws AuthenticationInvalidException;

    /**
     * 토큰 검사
     *
     * @param accessToken 액세스토큰
     * @param refreshToken 리프레쉬토큰
     * @return accessToken
     */
    String validateToken(String accessToken, String refreshToken);
}
