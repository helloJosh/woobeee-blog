package com.woobeee.blog.post.dto;

import java.util.List;

public record PostUpdateRequest(
        String title,
        String context,
        List<Long> categories,
        List<Long> tags,
        List<Long> images) {
}
