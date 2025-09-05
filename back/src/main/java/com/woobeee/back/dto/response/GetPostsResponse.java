package com.woobeee.back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostsResponse {
    boolean hasNext;
    List<PostContent> contents;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostContent {
        private Long id;
        private String title;
        private String content;
        private String categoryName;
        private Long categoryId;
        private Long views;
        private Long likes;
        private LocalDateTime createdAt;
    }
}
