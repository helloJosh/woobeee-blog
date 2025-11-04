package com.woobeee.chatv2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker

@SpringBootApplication
@EnableWebSocketMessageBroker
class ChatV2Application

fun main(args: Array<String>) {
    runApplication<ChatV2Application>(*args)
}
