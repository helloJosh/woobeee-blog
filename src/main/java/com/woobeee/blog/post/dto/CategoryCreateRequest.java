package com.woobeee.blog.post.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryCreateRequest(
        @NotNull List<CategoryRequest> categories
) {
}
