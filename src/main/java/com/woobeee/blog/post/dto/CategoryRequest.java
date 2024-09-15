package com.woobeee.blog.post.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryRequest(
        @NotNull Long id,
        @NotNull String name,
        List<CategoryRequest> children
) {
}
