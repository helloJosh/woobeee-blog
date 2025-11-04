package com.woobeee.chatv2.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostExportDto(
    val id: Long?,
    val url: String?,
    val titleKo: String?,
    val titleEn: String?,
    val textKo: String?,
    val textEn: String?,
    val views: Long?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val categoryNameKo: String?,
    val categoryNameEn: String?
)
