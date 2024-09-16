package com.woobeee.blog.post.dto.request;


import lombok.Builder;


@Builder
public record TagUpdateRequest(
        String oldTagName,
        String newTagName
) {
}
