package com.woobeee.blog.post.controller;

import com.woobeee.blog.api.Response;
import com.woobeee.blog.post.dto.request.CommentCreateRequest;
import com.woobeee.blog.post.dto.request.CommentUpdateRequest;
import com.woobeee.blog.post.dto.response.CommentReadAllResponse;
import com.woobeee.blog.post.exception.CommentCreateFromNotValidException;
import com.woobeee.blog.post.exception.CommentUpdateFromNotValidException;
import com.woobeee.blog.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public Response<Void> saveComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest commentCreateRequest,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            throw new CommentCreateFromNotValidException("댓글 생성 폼이 유효하지 않습니다.");
        }

        commentService.create(commentCreateRequest);

        return Response.createSuccess();
    }

    @GetMapping("/posts/{postId}/comments")
    public Response<CommentReadAllResponse> readAllComments(
            @PathVariable Long postId
    ) {
        return Response.success(commentService.readAll(postId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public Response<Void> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            throw new CommentUpdateFromNotValidException("댓글 생성 폼이 유효하지 않습니다.");
        }
        commentService.update(commentUpdateRequest);

        return Response.success();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public Response<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.delete(commentId);

        return Response.deleteSuccess();
    }
}
