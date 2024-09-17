package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * 카테고리 조회 응답.
 *
 * @param id 카테고리 아이디
 * @param name 카테고리 이름
 * @param children 자식 카테고리
 */
@Builder
public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> children
) {
}
