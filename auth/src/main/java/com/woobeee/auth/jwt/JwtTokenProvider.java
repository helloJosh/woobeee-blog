package com.woobeee.auth.jwt;

import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.JwtExpiredException;
import com.woobeee.auth.exception.JwtNotValidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {
    private final long accessTokenExpirationMillis = 1000 * 60 * 60; // 1시간
    private final SecretKey accessTokenKey;

    public JwtTokenProvider(
            @Value("${jwt.access-token.secret}") String secret
    ) {
        this.accessTokenKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(List<AuthType> authTypes, String loginId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .subject(loginId)
                .claim("roles", authTypes)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(accessTokenKey)
                .compact();
    }

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

    public Authentication getAuthentication(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(accessTokenKey).build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            List<String> roles = claims.get("roles", List.class); // roles는 배열로 저장되어야 함

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new UsernamePasswordAuthenticationToken(userId, null, authorities);

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("login.jwtExpired");
        } catch (JwtException e) {
            throw new JwtNotValidException("login.jwtInvalid");
        }
    }
}