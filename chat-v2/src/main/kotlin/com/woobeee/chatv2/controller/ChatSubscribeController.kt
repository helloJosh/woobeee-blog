package com.woobeee.chatv2.controller

import com.woobeee.chatv2.dto.ChatMessageDto
import com.woobeee.chatv2.service.ChatService
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class ChatSubscribeController(
    private val chatService: ChatService
) {

    // 클라가 /topic/conversation/{id} 를 구독할 때 한 번 실행됨
    @SubscribeMapping("/conversation")
    fun onSubscribe(headerAccessor: SimpMessageHeaderAccessor)
    : List<ChatMessageDto> {
        val userId = headerAccessor.sessionAttributes?.get("userId") as? String
            ?: throw IllegalStateException("no userId in session")


        return chatService.getHistory(userId)
    }
}