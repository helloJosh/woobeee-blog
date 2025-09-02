package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetCommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/back/comments")
@Tag(name = "Comment Controller", description = "댓글 컨트롤러")
public interface CommentController {
    @Operation(
            summary = "댓글 저장 API"
    )
    @PostMapping("")
    ApiResponse<Void> saveComment(
            @RequestHeader(name = "userId", required = false) String userId,
            @RequestBody PostCommentRequest request
    );

    @Operation(
            summary = "댓글 삭제 API"
    )
    @DeleteMapping("/{commentId}")
    ApiResponse<Void> deleteComment(
            @PathVariable(value = "commentId") Long commentId,
            @RequestHeader(name = "userId", required = false) String userId
    );

    @Operation(
            summary = "댓글 조회 API"
    )
    @GetMapping("/{postId}")
    ApiResponse<List<GetCommentResponse>> getAllCommentsFromPost(
            @PathVariable(value = "postId") Long postId,
            @RequestHeader(name = "userId", required = false) String userId
    );
}
