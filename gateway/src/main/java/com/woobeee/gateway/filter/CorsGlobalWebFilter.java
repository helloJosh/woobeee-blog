package com.woobeee.gateway.filter;

import com.woobeee.gateway.config.CorsConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsGlobalWebFilter {
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

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
