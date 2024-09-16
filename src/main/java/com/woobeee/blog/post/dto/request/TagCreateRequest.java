package com.woobeee.blog.post.dto.request;


import lombok.Builder;

import java.util.List;

@Builder
public record TagCreateRequest(
        List<String> tags
) {
}
