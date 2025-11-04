package com.woobeee.chatv2.controller

import com.woobeee.chatv2.dto.ChatMessageDto
import com.woobeee.chatv2.service.ChatService
import lombok.RequiredArgsConstructor
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
@RequiredArgsConstructor
class ChatSendController(
    private val chatService: ChatService
) {

    /**
     * WebSocket Message 전송 컨트롤러
     */
    @MessageMapping("/chat.send")  // /app/chat.send 로 들어옴
    fun onUserMessage(
        msg: ChatMessageDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        // HandshakeInterceptor에서 넣었던 값 꺼내기
        val userId = headerAccessor
            .sessionAttributes?.get("userId") as? String
            ?: throw IllegalStateException("userId not found in websocket session")

        // 1. DB에 유저 메시지 저장
        chatService.saveMessage(msg);

        // 2. 같은 대화방에 브로드캐스트
        chatService.broadcastSendMessage(msg);

        // 3. AI 호출은 서비스에서 비동기로 처리하고,
        //    답이 오면 아래처럼 같은 채널로 다시 보내면 됨
        //    답 저장
        chatService.askAiAndSaveMessage(msg);

        // 4. AI 응답 브로드캐스트
        chatService.broadcastSendMessage(msg);
    }
}