package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostsResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    void savePost(PostPostRequest request, UUID userId);
    void deletePost(Long postId, UUID userId);

    GetPostsResponse getAllPost(String q, String locale, Pageable pageable);
    GetPostsResponse getAllPost(String q, String locale, Long categoryId, Pageable pageable);
}
