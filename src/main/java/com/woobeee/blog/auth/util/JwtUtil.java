package com.woobeee.blog.auth.util;

import com.woobeee.blog.auth.exception.AccessTokenNotValidException;
import com.woobeee.blog.auth.exception.RefreshTokenExpireException;
import com.woobeee.blog.auth.exception.RefreshTokenNotValidException;
import com.woobeee.blog.auth.repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Jwt 유틸 클래스.
 *
 * @author 김병우
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final TokenRepository tokenRepository;
    private final SecretKey accessTokenKey = Keys.hmacShaKeyFor("test-aceasdfsdf-secure-key-for-access-token".getBytes());
    private final SecretKey refreshTokenKey = Keys.hmacShaKeyFor("test-aceasdfsdf-secure-key-for-refresh-token".getBytes());
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; //1주일

    /**
     * 액세스 토큰 생성.
     *
     * @param loginId 로그인 아이디
     * @return 엑세스 토큰 반환
     */
    public String generateAccessToken(String loginId) {
        String accessToken = Jwts.builder()
                .subject(loginId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(accessTokenKey)
                .compact();

        tokenRepository.saveAccessToken(accessToken, loginId);
        log.info("AccessToken : {}", accessToken);
        return  accessToken;
    }

    /**
     * 리프레쉬 토큰 생성
     *
     * @param loginId 로그인아이디
     * @return 리프레쉬 토큰 반환
     */
    public String generateRefreshToken(String loginId) {
        String refreshToken =  Jwts.builder()
                .subject(loginId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(refreshTokenKey)
                .compact();

        tokenRepository.saveRefreshToken(refreshToken, loginId);

        log.info("RefreshToken : {}", refreshToken);
        return refreshToken;
    }

    /**
     * 액세스 토큰 확인, 만료시 리프레쉬 토큰 확인 후 재발급.
     *
     * @param accessToken 액세스 토큰
     * @return 액세스 토큰 혹은 재발급된 액세스 토큰 반환
     */
    public String validateAccessToken(String accessToken, String refreshToken) {
        String tokenLoginId = null;

        try {
            tokenLoginId = Jwts.parser()
                    .verifyWith(accessTokenKey).build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .getSubject();

        } catch (ExpiredJwtException e) {

            tokenLoginId = validateRefreshToken(refreshToken);
            accessToken = generateAccessToken(tokenLoginId);

            tokenRepository.saveAccessToken(accessToken, tokenLoginId);

        } catch (JwtException e) {
            throw new AccessTokenNotValidException(accessToken + " : 유효하지 않는 액세스 토큰입니다.");
        }
        return tokenLoginId;
    }

    /**
     * 리프레쉬 토큰 확인.
     *
     * @param refreshToken 리프레쉬 토큰
     */
    public String validateRefreshToken(String refreshToken) {
        String tokenLoginId = null;

        try {
            tokenLoginId = Jwts.parser()
                    .verifyWith(refreshTokenKey).build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpireException("리프레쉬 토큰이 만료되었습니다. 다시 로그인해주세요");
        } catch (JwtException e) {
            throw new RefreshTokenNotValidException(refreshToken + " : 유효하지 않는 리프레쉬 토큰입니다.");
        }
        return tokenLoginId;
    }

}
