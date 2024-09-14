package com.woobeee.blog.post.dto;

import com.woobeee.blog.post.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record PostReadRequest(
        String title,
        String context,
        Status status,
        Long count,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
