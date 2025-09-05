package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    void savePost(PostPostRequest request, String loginId);
    void deletePost(Long postId, String loginId);
    GetPostsResponse getAllPost(String q, String locale, Long categoryId, Pageable pageable);
    GetPostResponse getPost(Long postId, String locale, String loginId);
}
