package com.woobeee.blog.post.dto.request;

import lombok.Builder;

import java.util.List;

/**
 * 게시글 수정 요청.
 *
 * @param postId 게시글 아이디
 * @param title 제목
 * @param context 내용
 * @param categories 카테고리 요청 리스트
 * @param tags 태그 리스트
 */
@Builder
public record PostUpdateRequest(
        Long postId,
        String title,
        String context,
        List<CategoryRequest> categories,
        List<String> tags) {
}
