package com.woobeee.blog.post.dto.request;


import lombok.Builder;

/**
 * 태그 수정 요청
 *
 * @param oldTagName 수정 전 태그이름
 * @param newTagName 수정 후 태그 이름
 */
@Builder
public record TagUpdateRequest(
        String oldTagName,
        String newTagName
) {
}
