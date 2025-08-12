package com.woobeee.gateway.jwt;

import com.woobeee.gateway.exception.JwtExpiredException;
import com.woobeee.gateway.exception.JwtNotValidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtTokenProvider {
    private final SecretKey accessTokenKey = Keys.hmacShaKeyFor("test-secure-key-for-access-token".getBytes());

    public String getUserId(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(accessTokenKey).build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("login.jwtExpired");
        } catch (JwtException e) {
            throw new JwtNotValidException("login.jwtInvalid");
        }
    }
}