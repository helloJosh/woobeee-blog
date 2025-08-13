package com.woobeee.back.controller;

import com.woobeee.back.support.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class LikeControllerImpl implements LikeController{

    ///GET /api/back/like
    @Override
    public ApiResponse<Void> getLike() {
        log.info("get like request receive");
        return ApiResponse.success("HI");
    }

    ///POST /api/back/like
    @Override
    public ApiResponse<Void> postLike() {
        log.info("post like request receive");
        return ApiResponse.success("HI");
    }

}
