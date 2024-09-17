package com.woobeee.blog.post.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * 카테고리 요청.
 *
 * @param id 카테고리 아이디
 * @param name 카테고리 이름
 * @param children 자식 카테고리 요청 폼.
 */
@Builder
public record CategoryRequest(
        @NotNull Long id,
        @NotNull String name,
        List<CategoryRequest> children
) {
}
