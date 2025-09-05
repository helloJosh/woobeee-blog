package com.woobeee.back.controller;


import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.GetPostResponse;
import com.woobeee.back.dto.response.GetPostsResponse;
import com.woobeee.back.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/back/posts")
@Tag(name = "Post Controller", description = "게시글 컨트롤러")
public interface PostController {
    @Operation(
            summary = "전체 게시글 조회 API"
    )
    @GetMapping()
    ApiResponse<GetPostsResponse> getPosts(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestHeader(name = "loginId", required = false) String loginId,
            @RequestHeader(name = "Accept-Language", defaultValue = "ko-KR") String locale
    );

    @Operation(
            summary = "게시글 조회 API"
    )
    @GetMapping("/{postId}")
    ApiResponse<GetPostResponse> getPost(
            @PathVariable("postId") Long postId,
            @RequestHeader(name = "loginId", required = false) String loginId,
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
