package com.woobeee.back.dto.request;

import lombok.Builder;

@Builder
public record PostPostRequest(
        String titleKo,
        String titleEn,
        Long categoryId
) {
}