package com.woobeee.blog.post.dto;


import java.util.List;

public record PostCreateRequest(
        String title,
        String context,
        CategoryRequest category,
        List<String> tags
) {
}
