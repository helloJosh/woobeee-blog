package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.request.PostPostRequest;
import com.woobeee.back.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/back/comments")
@Tag(name = "Comment Controller", description = "댓글 컨트롤러")
public interface CommentController {
    @Operation(
            summary = "댓글 저장 API"
    )
    @PostMapping("/{parentId}")
    ApiResponse<Void> saveComment(
            @PathVariable(value = "parentId") Long parentId,
            @RequestBody PostCommentRequest request
    );

    @Operation(
            summary = "댓글 삭제 API"
    )
    @DeleteMapping("/{commentId}")
    ApiResponse<Void> deleteComment(
            @PathVariable(value = "commentId") Long commentId
    );
}
