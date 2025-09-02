package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.service.PostService;
import com.woobeee.back.support.CustomPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PostControllerImpl implements PostController {
    private final PostService postService;

    @Override
    public ApiResponse<GetPostsResponse> getPostWithCategory (
            String q,
            Long categoryId,
            Integer page,
            Integer size,
            String userId,
            String locale
    ) {
        log.info("getPostWithCategory request received");
        CustomPageable pageable = new CustomPageable(page, size);

        return ApiResponse.success(
                postService.getAllPost(q, locale, categoryId, pageable),
                "post get with category success"
        );
    }

    @Override
    public ApiResponse<GetPostsResponse> getPosts(
            String q,
            Integer page,
            Integer size,
            String userId,
            String locale
    ) {
        log.info("getPosts request received");

        CustomPageable pageable = new CustomPageable(page, size);

        return ApiResponse.success(
                postService.getAllPost(q, locale, pageable),
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
