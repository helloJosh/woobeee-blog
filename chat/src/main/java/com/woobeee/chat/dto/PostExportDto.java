package com.woobeee.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostExportDto(
        Long id,
        String url,
        String titleKo,
        String titleEn,
        String textKo,
        String textEn,
        Long views,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String categoryNameKo,
        String categoryNameEn
) {}