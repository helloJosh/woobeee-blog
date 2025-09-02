package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.response.ApiResponse;
import com.woobeee.back.dto.response.GetCommentResponse;
import com.woobeee.back.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentControllerImpl implements CommentController {
    private final CommentService commentService;

    @Override
    public ApiResponse<Void> saveComment (
            String userId,
            PostCommentRequest request
    ) {
        log.info("saveComment request userId = {}", userId);

        commentService.saveComment(request, userId);

        return ApiResponse.createSuccess("comment saved successfully");
    }

    @Override
    public ApiResponse<Void> deleteComment (
            Long commentId,
            String userId
    ) {
        log.info("deleteComment request userId = {}", userId);

        commentService.deleteComment(commentId, userId);

        return ApiResponse.success("comment deleted successfully");
    }

    @Override
    public ApiResponse<List<GetCommentResponse>> getAllCommentsFromPost(Long postId, String userId) {
        log.info("get comment request userId = {}", userId);

        return ApiResponse.success(
                commentService.getAllCommentsFromPost(postId, userId),
                "comment deleted successfully"
        );
    }
}
