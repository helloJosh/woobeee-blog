package com.woobeee.chatv2.dto


data class ChatRequest(
    val messages: List<ChatMessageDto>,
    val maxTokens: Int? = null
)