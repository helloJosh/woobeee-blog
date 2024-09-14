package com.woobeee.blog.post.dto;

import java.util.List;

public record PostUpdateRequest(
        Long postId,
        String title,
        String context,
        List<CategoryRequest> categoryRequest,
        List<String> tags) {
}
