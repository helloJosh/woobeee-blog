package com.woobeee.gateway.filter;

import com.woobeee.gateway.exception.JwtExpiredException;
import com.woobeee.gateway.exception.JwtNotValidException;
import com.woobeee.gateway.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider; // JWT 유틸 클래스 (직접 구현 필요)

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isWhitelistPath(path)) {
            log.info("Whitelist path found: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String userId = jwtTokenProvider.getUserId(token); // JWT에서 userId 추출

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("userId", userId)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (JwtExpiredException e) {
                // 로그만 남기고 통과시켜도 되고, 거절해도 됨
                log.warn("JWT 만료: {}", e.getMessage());
                return onError(exchange, "login.invalidIdOrPassword", HttpStatus.UNAUTHORIZED);
            } catch (JwtNotValidException e) {
                log.warn("JWT 유효하지않음: {}", e.getMessage());
                return onError(exchange, "login.jwtExpired", HttpStatus.FORBIDDEN);
            } catch (Exception e) {
                log.warn("JWT 처리 오류: {}", e.getMessage());
                return onError(exchange, "login.jwtInvalid", HttpStatus.BAD_REQUEST);
            }
        }

        return chain.filter(exchange); // 토큰 없으면 그냥 통과
    }

    @Override
    public int getOrder() {
        return -1; // 우선순위 높게 설정
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] bytes = ("{\"error\": \"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
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