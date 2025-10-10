package com.woobeee.chat.dto;


import java.util.List;

public record ChatRequest(List<ChatMessage> messages, Integer maxTokens) {}