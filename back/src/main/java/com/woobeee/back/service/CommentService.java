package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostCommentRequest;
import com.woobeee.back.dto.response.GetCommentResponse;

import java.util.List;

public interface CommentService {
    void saveComment(PostCommentRequest request, String userId);
    void deleteComment(Long commentId, String userId);
    List<GetCommentResponse> getAllCommentsFromPost(Long postId, String userId);
}
