package com.woobeee.back.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetPostResponse(
        Long id,
        String title,
        String content,
        String categoryName,
        Long categoryId,
        Long views,
        Long likes,
        Boolean isLiked,
        LocalDateTime createdAt
) {
}