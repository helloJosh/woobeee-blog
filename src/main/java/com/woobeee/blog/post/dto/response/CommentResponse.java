package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * 댓글 조회 응답.
 *
 * @param id 코맨트 아이디
 * @param context 댓글 내용
 * @param children 자식 댓글
 */
@Builder
public record CommentResponse(
        Long id,
        String context,
        List<CommentResponse> children) {
}
