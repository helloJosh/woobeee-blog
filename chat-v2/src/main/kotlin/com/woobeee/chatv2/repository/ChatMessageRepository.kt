package com.woobeee.chatv2.repository

import com.woobeee.chatv2.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findTop10BySenderOrderByCreatedAtDesc(sender: String): List<ChatMessage>
}