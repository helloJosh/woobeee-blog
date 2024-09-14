package com.woobeee.blog.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CommentReadAllResponse(
        List<CommentResponse> commentResponses
) {
}
