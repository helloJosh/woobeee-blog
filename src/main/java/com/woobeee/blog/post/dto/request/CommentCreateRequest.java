package com.woobeee.blog.post.dto.request;

import lombok.Builder;

@Builder
public record CommentCreateRequest(
        Long parentCommentId,
        Long postId,
        Long memberId,
        String context
) {
}
