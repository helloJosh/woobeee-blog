package com.woobeee.chatv2.dto

data class ChatMessageDto(
    val sender: String,
    val content: String,
    val type: String = "USER_MESSAGE_CREATED"
)
