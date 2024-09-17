package com.woobeee.blog.post.dto.request;

import lombok.Builder;

/**
 * 댓글 수정 요청.
 *
 * @param commentId 댓글아이디
 * @param oldContext 수정전 댓글 내용
 * @param newContext 수정후 댓글 내용
 */
@Builder
public record CommentUpdateRequest(
        Long commentId,
        String oldContext,
        String newContext
) {
}
