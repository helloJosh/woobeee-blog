package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * 카테고리 전체 조회 응답.
 *
 * @param categories 카테고리 응답 리스트
 */
@Builder
public record CategoryReadAllResponse(
        List<CategoryResponse> categories
) {
}
