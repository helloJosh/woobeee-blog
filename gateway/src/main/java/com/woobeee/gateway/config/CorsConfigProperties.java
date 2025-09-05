package com.woobeee.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.cloud.gateway.server.webflux.globalcors")
public class CorsConfigProperties {

    private Map<String, CorsMapping> corsConfigurations;

    @Getter
    @Setter
    public static class CorsMapping {
        private List<String> allowedOrigins;
    }
}
