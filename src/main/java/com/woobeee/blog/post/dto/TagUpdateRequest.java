package com.woobeee.blog.post.dto;


import java.util.List;

public record TagUpdateRequest(
        String oldTagName,
        String newTagName
) {
}
