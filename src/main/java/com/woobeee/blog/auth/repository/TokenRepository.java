package com.woobeee.blog.auth.repository;

/**
 * 토큰 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface TokenRepository {
    /**
     * 액세스 토큰 저장.
     *
     * @param accessToken 액서스 토큰
     * @param loginId 로그인 아이디
     */
    void saveAccessToken(String accessToken, String loginId);

    /**
     * 리프레쉬 토큰 저장.
     *
     * @param refreshToken 리프레쉬 토큰
     * @param loginId 로그인 아이디
     */
    void saveRefreshToken(String refreshToken, String loginId);

    /**
     * 액세스 토큰 읽기.
     *
     * @param loginId 로그인 아이디
     * @return token string value
     */
    String findAccessToken(String loginId);

    /**
     * 리프레쉬 토큰 읽기.
     *
     * @param loginId 로그인 아이디
     * @return token string value
     */
    String findRefreshToken(String loginId);
}
