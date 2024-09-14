package com.woobeee.blog.post.dto;

public record CommentCreateRequest(
        Long parentCommentId,
        Long postId,
        Long memberId,
        String context
) {
}
