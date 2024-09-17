package com.woobeee.blog.post.dto.request;

import lombok.Builder;

/**
 * 댓글 생성 요청.
 *
 * @param parentCommentId 부모 댓글 아이디
 * @param postId 게시글 아이디
 * @param memberId 맴버 아이디
 * @param context 댓글 내용
 */
@Builder
public record CommentCreateRequest(
        Long parentCommentId,
        Long postId,
        Long memberId,
        String context
) {
}
