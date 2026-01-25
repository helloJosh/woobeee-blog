package com.woobeee.back.dto.request;

import lombok.Builder;

@Builder
public record PostCategoryRequest(
        String nameKo,
        String nameEn
) {
}