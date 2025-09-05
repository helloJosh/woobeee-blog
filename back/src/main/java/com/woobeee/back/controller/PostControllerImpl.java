package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.service.PostService;
import com.woobeee.back.support.CustomPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
            String userId,
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
            String userId,
            String locale
    ) {
        log.info("getPost request received");

        return ApiResponse.success(
                postService.getPost(postId, locale, userId),
                "post get success"
        );
    }

    @Override
    public ApiResponse<Void> savePost(PostPostRequest request) {
        return null;
    }

    @Override
    public ApiResponse<Void> deletePost(Long postId) {
        return null;
    }
}
