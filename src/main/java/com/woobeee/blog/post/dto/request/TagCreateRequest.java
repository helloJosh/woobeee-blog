package com.woobeee.blog.post.dto.request;


import lombok.Builder;

import java.util.List;

/**
 * 태그 생성 요청.
 *
 * @param tags 태그 리스트
 */
@Builder
public record TagCreateRequest(
        List<String> tags
) {
}
