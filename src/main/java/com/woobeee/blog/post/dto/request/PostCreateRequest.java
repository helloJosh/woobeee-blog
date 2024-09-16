package com.woobeee.blog.post.dto.request;


import lombok.Builder;

import java.util.List;

@Builder
public record PostCreateRequest(
        String title,
        String context,
        List<CategoryRequest> categories,
        List<String> tags
) {
}
