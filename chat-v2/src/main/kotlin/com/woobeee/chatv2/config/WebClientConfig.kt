package com.woobeee.chatv2.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig (
    @Value("\${vllm.url}") private val url: String,
) {

    @Bean
    fun vllmWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .compress(true)
            .keepAlive(true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(60, TimeUnit.SECONDS))
            }

        return WebClient.builder()
            .baseUrl(url)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
                    .build()
            )
            .build()
    }
}