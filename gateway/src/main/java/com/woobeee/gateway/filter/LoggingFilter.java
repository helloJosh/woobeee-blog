package com.woobeee.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
        int port = remoteAddress != null ? remoteAddress.getPort() : -1;

        log.info(">>> Incoming request: [{}] {} from {}:{}", request.getMethod(), request.getURI(), ip, port);

        return chain.filter(exchange)
                .doOnSuccess(res -> log.info("<<< Completed response: {} for {} {}",
                        exchange.getResponse().getStatusCode(),
                        request.getMethod(), request.getURI()));
    }
}