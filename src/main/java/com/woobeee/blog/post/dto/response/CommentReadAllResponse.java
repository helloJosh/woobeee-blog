package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;


/**
 * 댓글 전체 읽기.
 *
 * @param postId 개시글 아이디
 * @param commentResponses 댓글 응답 폼
 */
@Builder
public record CommentReadAllResponse(
        Long postId,
        List<CommentResponse> commentResponses
) {
}
