package com.woobeee.blog.post.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CategoryRequest(
        @NotNull Long id,
        @NotNull String name,
        List<CategoryRequest> children
) {
}
