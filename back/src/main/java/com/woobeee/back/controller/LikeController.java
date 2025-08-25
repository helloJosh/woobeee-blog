package com.woobeee.back.controller;


import com.woobeee.back.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/back/likes")
@Tag(name = "Like Controller", description = "좋아요 컨트롤러")
public interface LikeController {
    @Operation(
            summary = "좋아요 등록 API"
    )
    @PostMapping("/{postId}")
    ApiResponse<Void> saveLike(
            @PathVariable(value = "postId") Long postId,
            @RequestHeader(name = "userId", required = false) String userId
    );

    @Operation(
            summary = "좋아요 취소 API"
    )
    @DeleteMapping("/{postId}")
    ApiResponse<Void> deleteLike(
            @PathVariable(value = "postId") Long postId,
            @RequestHeader(name = "userId", required = false) String userId
    );
}
