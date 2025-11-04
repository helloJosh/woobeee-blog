package com.woobeee.chatv2.service

import com.woobeee.chatv2.adapter.VllmAdaptor
import com.woobeee.chatv2.dto.ChatMessageDto
import com.woobeee.chatv2.entity.ChatMessage
import com.woobeee.chatv2.repository.ChatMessageRepository
import lombok.RequiredArgsConstructor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
@RequiredArgsConstructor
class ChatService (
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatMessageRepository: ChatMessageRepository,
    private val vllmAdaptor: VllmAdaptor
) {
    // 예시용 In-Memory 저장소 (실제론 JPA Repo 쓰면 됨)
    // key: userId(or conversationId), value: 그 유저의 메시지 리스트
    private val store: MutableMap<String, MutableList<ChatMessageDto>> = ConcurrentHashMap()

    /**
     * AI 질문(=사용자 메시지) 저장
     */
    fun saveMessage(chatMessage: ChatMessageDto): ChatMessageDto {
        val entity = ChatMessage(
            sender = chatMessage.sender,
            content = chatMessage.content,
            type = chatMessage.type
        )
        chatMessageRepository.save(entity)
        return chatMessage
    }

    /**
     * AI에게 물어보고 그 응답을 저장하는 메서드
     * 여기서는 실제 AI 호출 대신 더미로 만듦
     */
    fun askAiAndSaveMessage(userMessage: ChatMessageDto): ChatMessageDto {
        // 실제로는 여기서 AI API 호출해서 답변 받아옴
        val aiReply = ChatMessageDto(
            sender = "AI",
            content = "이건 더미 응답입니다: \"${userMessage.content}\" 에 대한 답변이에요.",
            type = "ASSISTANT_MESSAGE"
        )

        val entity = ChatMessage(
            sender = aiReply.sender,
            content = aiReply.content,
            type = aiReply.type
        )
        chatMessageRepository.save(entity)

        return aiReply
    }

    /**
     * 방금 만들어진/저장된 메시지를 해당 conversation으로 브로드캐스트
     */
    fun broadcastSendMessage(chatMessage: ChatMessageDto) {
        // 프론트에서는 /topic/conversation/{conversationId} 구독하고 있어야 함
        messagingTemplate.convertAndSend(
            "/topic/conversation/${chatMessage.sender}",
            chatMessage
        )
    }

    /**
     * 과거 챗 전체 조회
     */
    fun getHistory(userId: String): List<ChatMessageDto> {
        return chatMessageRepository
            .findTop10BySenderOrderByCreatedAtDesc(userId)
            .map {
                ChatMessageDto(
                    sender = it.sender,
                    content = it.content,
                    type = it.type
                )
            }
    }
}