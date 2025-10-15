package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PostService {
    void savePost (
            PostPostRequest request,
            String loginId,
            MultipartFile markdownEn,
            MultipartFile markdownKr,
            List<MultipartFile> files
    );

    void deletePost(Long postId, String loginId);
    GetPostsResponse getAllPost(String q, String locale, Long categoryId, Pageable pageable);
    GetPostResponse getPost(Long postId, String locale, String loginId, HttpServletRequest request);
}
