package com.woobeee.back.controller;

import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class LikeControllerImpl implements LikeController{
    private final LikeService likeService;

    @Override
    public ApiResponse<Void> saveLike(Long postId, String userId) {
        log.info("saveLike request postId:{}, userId:{}", postId, userId);

        likeService.saveLike(postId, userId);

        return ApiResponse.success(
                "save like success"
        );
    }

    @Override
    public ApiResponse<Void> deleteLike(Long postId, String userId) {
        log.info("deleteLike request postId:{}, userId:{}", postId, userId);

        likeService.deleteLike(postId, userId);

        return ApiResponse.success(
                "delete like success"
        );
    }
}
