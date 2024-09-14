package com.woobeee.blog.post.dto;

import java.util.List;

public record CategoryRequest(
        Long id,
        String name,
        List<CategoryRequest> children
) {
}
