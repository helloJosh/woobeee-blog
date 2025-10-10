package com.woobeee.back.dto.batch;


import java.time.LocalDateTime;
import java.util.UUID;

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