package com.woobeee.blog.post.dto;

public record CommentUpdateRequest(
        Long commentId,
        String oldContext,
        String newContext
) {
}
