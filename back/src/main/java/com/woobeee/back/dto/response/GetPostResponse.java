package com.woobeee.back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostResponse {
    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private Long categoryId;
    private Long views;
    private Long likes;
    private Boolean isLiked;
    private LocalDateTime createdAt;
}
