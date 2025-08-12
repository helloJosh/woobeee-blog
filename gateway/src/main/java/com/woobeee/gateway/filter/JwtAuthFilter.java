package com.woobeee.gateway.filter;

import com.woobeee.gateway.exception.JwtExpiredException;
import com.woobeee.gateway.exception.JwtNotValidException;
import com.woobeee.gateway.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 화이트리스트는 패스
        if (isWhitelistPath(path)) {
            log.debug("Whitelist path: {}", path);
            return chain.filter(exchange);
        }

        // Authorization 헤더 확인
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "login.jwtMissing", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            // 토큰 파싱 및 검증 (만료/서명/포맷 등)
            String userId = jwtTokenProvider.getUserId(token);

            // 하위 서비스로 userId 전달
            ServerHttpRequest mutated = request.mutate()
                    .header("userId", userId)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (JwtExpiredException e) {
            log.warn("JWT expired: {}", e.getMessage());
            return onError(exchange, "login.jwtExpired", HttpStatus.UNAUTHORIZED);
        } catch (JwtNotValidException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            return onError(exchange, "login.jwtInvalid", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.warn("JWT processing error: {}", e.getMessage());
            return onError(exchange, "login.jwtError", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 아주 이른 시점에 실행
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        byte[] body = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    private boolean isWhitelistPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/network")
                || path.startsWith("/docs")
                || path.startsWith("/static")
                || path.startsWith("/openapi.json")
                || path.startsWith("/v1/network/sm/login")
                || path.startsWith("/v1/network/sm/signup")
                || path.startsWith("/sm")
                || path.startsWith("/dm-batch")
                || path.startsWith("/dm-consumer");
    }
}