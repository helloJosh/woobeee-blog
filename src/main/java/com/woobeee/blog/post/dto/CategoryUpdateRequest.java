package com.woobeee.blog.post.dto;


import java.util.List;

public record CategoryUpdateRequest(
        String oldCategoryName,
        String newCategoryName
) {
}
