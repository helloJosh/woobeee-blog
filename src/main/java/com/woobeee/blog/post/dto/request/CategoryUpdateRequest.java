package com.woobeee.blog.post.dto.request;


import lombok.Builder;


@Builder
public record CategoryUpdateRequest(
        String oldCategoryName,
        String newCategoryName
) {
}
