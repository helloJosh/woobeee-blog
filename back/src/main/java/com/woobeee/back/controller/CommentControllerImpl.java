package com.woobeee.back.controller;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentControllerImpl implements CommentController {


    @Override
    public ApiResponse<Void> saveComment(Long parentId, PostCommentRequest request) {
        return null;
    }

    @Override
    public ApiResponse<Void> deleteComment (
            Long commentId) {
        return null;
    }
}
