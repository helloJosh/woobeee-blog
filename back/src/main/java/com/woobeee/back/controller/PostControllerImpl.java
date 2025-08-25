package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetPostWithCategoryResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.support.CustomPageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PostControllerImpl implements PostController {
    @Override
    public ApiResponse<List<GetPostWithCategoryResponse>> getPostWithCategory (
            Long categoryId,
            Integer page,
            Integer size,
            String userId,
            String locale
    ) {
        log.info("getPostWithCategory request received");

        CustomPageable pageable = new CustomPageable(page, size);

        return null;
    }

    @Override
    public ApiResponse<List<GetPostsResponse>> getPosts(
            Integer page,
            Integer size,
            String userId,
            String locale
    ) {
        log.info("getPosts request received");

        CustomPageable pageable = new CustomPageable(page, size);

        return null;
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
