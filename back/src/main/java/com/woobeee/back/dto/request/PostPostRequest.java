package com.woobeee.back.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPostRequest {
    private String titleKo;
    private String titleEn;
    private Long categoryId;
}
