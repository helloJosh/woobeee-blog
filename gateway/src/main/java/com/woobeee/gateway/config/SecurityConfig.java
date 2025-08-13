package com.woobeee.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/back/like/**", "/api/back/comment/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/back/like/**", "/api/back/comment/**").authenticated()
                        //.pathMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN PAGE 아직없음
                        .anyExchange().permitAll()
                )
                .build();
    }
}
