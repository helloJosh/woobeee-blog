package com.woobeee.blog.post.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * 카테고리 생성 요청.
 *
 * @param categories 카테고리 요청 리스트
 */
@Builder
public record CategoryCreateRequest(
        @NotNull List<CategoryRequest> categories
) {
}
