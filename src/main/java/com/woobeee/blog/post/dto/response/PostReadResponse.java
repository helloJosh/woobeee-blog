package com.woobeee.blog.post.dto.response;

import com.woobeee.blog.post.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostReadResponse(
        String title,
        String context,
        Status status,
        Long count,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
