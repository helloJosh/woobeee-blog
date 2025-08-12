package com.woobeee.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsGlobalConfig {
    private final CorsConfigProperties corsConfigProperties;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        var corsMap = corsConfigProperties.getCorsConfigurations();
        var mapping = corsMap.get("/**");

        if (mapping != null && mapping.getAllowedOrigins() != null) {
            mapping.getAllowedOrigins().forEach(config::addAllowedOrigin);
            log.info("allowedOrigins: {}", mapping.getAllowedOrigins());
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS 등 모두 포함
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
