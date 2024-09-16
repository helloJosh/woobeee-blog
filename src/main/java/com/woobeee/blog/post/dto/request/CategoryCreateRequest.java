package com.woobeee.blog.post.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CategoryCreateRequest(
        @NotNull List<CategoryRequest> categories
) {
}
