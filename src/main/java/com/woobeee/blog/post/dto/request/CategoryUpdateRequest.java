package com.woobeee.blog.post.dto.request;


import lombok.Builder;

/**
 * 카테고리 수정 요청.
 *
 * @param oldCategoryName
 * @param newCategoryName
 */
@Builder
public record CategoryUpdateRequest(
        String oldCategoryName,
        String newCategoryName
) {
}
