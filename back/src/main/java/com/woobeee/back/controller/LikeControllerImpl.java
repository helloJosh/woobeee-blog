package com.woobeee.back.controller;

import com.woobeee.back.aop.Idempotent;
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
    @Idempotent
    public ApiResponse<Void> saveLike(Long postId, String loginId) {
        log.info("saveLike request postId:{}, userId:{}", postId, loginId);

        likeService.saveLike(postId, loginId);

        return ApiResponse.success(
                "save like success"
        );
    }

    @Override
    public ApiResponse<Void> deleteLike(Long postId, String loginId) {
        log.info("deleteLike request postId:{}, userId:{}", postId, loginId);

        likeService.deleteLike(postId, loginId);

        return ApiResponse.success(
                "delete like success"
        );
    }
}
