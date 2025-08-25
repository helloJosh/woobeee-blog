package com.woobeee.back.controller;


import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostWithCategoryResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/back/posts")
@Tag(name = "Post Controller", description = "게시글 컨트롤러")
public interface PostController {
    @Operation(
            summary = "카테고리별 게시글 조회 API"
    )
    @GetMapping("/{categoryId}")
    ApiResponse<List<GetPostWithCategoryResponse>> getPostWithCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestHeader(name = "userId", required = false) String userId,
            @RequestHeader(name = "Accept-Language", defaultValue = "ko-KR") String locale
    );

    @Operation(
            summary = "전체 게시글 조회 API"
    )
    @GetMapping()
    ApiResponse<List<GetPostsResponse>> getPosts(
            @RequestParam("page") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestHeader(name = "userId", required = false) String userId,
            @RequestHeader(name = "Accept-Language", defaultValue = "ko-KR") String locale
    );

    @Operation(
            summary = "게시글 저장 API"
    )
    @PostMapping()
    ApiResponse<Void> savePost(
            @RequestBody PostPostRequest request
    );

    @Operation(
            summary = "게시글 삭제 API"
    )
    @DeleteMapping("/{postId}")
    ApiResponse<Void> deletePost(
            @PathVariable(value = "postId") Long postId
    );
}
