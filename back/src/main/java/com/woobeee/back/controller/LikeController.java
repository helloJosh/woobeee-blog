package com.woobeee.back.controller;


import com.woobeee.back.support.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/back")
@Tag(name = "Dataset Dashboard Controller", description = "데이터 셋 대시보드 컨트롤러")
public interface LikeController {
    @Operation(
            summary = "좋아요 조회 API"
    )
    @GetMapping("/like")
    ApiResponse<Void> getLike();

    @Operation(
            summary = "좋아요 등록 API"
    )
    @PostMapping("/like")
    ApiResponse<Void> postLike();

}
