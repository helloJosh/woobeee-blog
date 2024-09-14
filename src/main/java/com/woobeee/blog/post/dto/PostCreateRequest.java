package com.woobeee.blog.post.dto;


import java.util.List;

public record PostCreateRequest(
        String title,
        String context,
        List<CategoryRequest> categories,
        List<String> tags
) {
}
