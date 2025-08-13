package com.woobeee.gateway.filter;

import com.woobeee.gateway.exception.JwtExpiredException;
import com.woobeee.gateway.exception.JwtNotValidException;
import com.woobeee.gateway.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;



@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("JwtAuthFilter message receive: {}", exchange.getRequest().getURI());

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                Mono.just(new SecurityContextImpl(authentication))
                        ));
            } catch (
                    JwtExpiredException | JwtNotValidException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}