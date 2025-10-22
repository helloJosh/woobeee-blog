package com.woobeee.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${vllm.url}")
    private String url;

    @Value("${vllm.username}")
    private String username;

    @Value("${vllm.password}")
    private String password;

    @Bean
    WebClient vllmWebClient() {
        var http = HttpClient.create()
                .compress(true)
                .keepAlive(true);

//        String authHeader = "Basic " + Base64.getEncoder().encodeToString(
//                (username + ":" + password).getBytes(StandardCharsets.UTF_8)
//        );

        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(http))
                //.defaultHeader("Authorization", authHeader)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg
                                -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }
}