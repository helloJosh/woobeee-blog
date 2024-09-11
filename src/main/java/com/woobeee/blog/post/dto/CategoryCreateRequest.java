package com.woobeee.blog.post.dto;


import java.util.List;

public record CategoryCreateRequest(
        List<CategoryRequest> categories
) {
}
