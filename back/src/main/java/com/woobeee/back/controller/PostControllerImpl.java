package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.service.PostService;
import com.woobeee.back.support.CustomPageable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PostControllerImpl implements PostController {
    private final PostService postService;

    @Override
    public ApiResponse<GetPostsResponse> getPosts(
            String q,
            Long categoryId,
            Integer page,
            Integer size,
            String loginId,
            String locale
    ) {
        log.info("getPosts request received");

        CustomPageable pageable = new CustomPageable(page, size);

        return ApiResponse.success(
                postService.getAllPost(q, locale, categoryId, pageable),
                "post get success"
        );
    }

    @Override
    public ApiResponse<GetPostResponse> getPost (
            Long postId,
            String loginId,
            String locale,
            HttpServletRequest request
    ) {
        log.info("getPost request received");

        return ApiResponse.success(
                postService.getPost(postId, locale, loginId, request),
                "post get success"
        );
    }

    @Override
    public ApiResponse<Void> savePost (
            PostPostRequest request,
            MultipartFile markdownEn,
            MultipartFile markdownKr,
            List<MultipartFile> files
    ) {
        log.info("save Post request received");

        postService.savePost(request, "kimjoshua135@gmail.com", markdownEn, markdownKr, files);

        return ApiResponse.createSuccess(
            "post save success"
        );
    }

    @Override
    public ApiResponse<Void> deletePost(Long postId) {
        return null;
    }
}
