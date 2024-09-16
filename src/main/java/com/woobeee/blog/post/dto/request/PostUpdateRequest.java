package com.woobeee.blog.post.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PostUpdateRequest(
        Long postId,
        String title,
        String context,
        List<CategoryRequest> categoryRequest,
        List<String> tags) {
}
