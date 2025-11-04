package com.woobeee.chatv2.interceptor

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception
import java.util.*

@Component
class AuthHandshakeInterceptor : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {

        val servletRequest = (request as ServletServerHttpRequest).servletRequest


        val userId = servletRequest.getHeader("userId")?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        attributes["userId"] = userId
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // DO NOTHING HAHA THIS METHOD IS WOLGEUB LUPANG
    }
}