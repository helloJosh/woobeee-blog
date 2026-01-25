package com.woobeee.back.dto.request;

import lombok.Builder;

@Builder
public record PostCommentRequest(
        Long postId,
        Long parentId,
        String content
) {
}