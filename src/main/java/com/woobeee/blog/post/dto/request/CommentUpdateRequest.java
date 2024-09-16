package com.woobeee.blog.post.dto.request;

import lombok.Builder;

@Builder
public record CommentUpdateRequest(
        Long commentId,
        String oldContext,
        String newContext
) {
}
