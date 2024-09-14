package com.woobeee.blog.post.dto;


import java.util.List;

public record TagCreateRequest(
        List<String> tags
) {
}
