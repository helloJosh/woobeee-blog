package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> children
) {
}
