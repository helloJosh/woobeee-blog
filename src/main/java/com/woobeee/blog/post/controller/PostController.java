package com.woobeee.blog.post.controller;

import com.woobeee.blog.api.Response;
import com.woobeee.blog.post.dto.CategoryCreateRequest;
import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.dto.response.PostReadResponse;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.exception.CategoryCreateFromNotValidException;
import com.woobeee.blog.post.exception.PostCreateFromNotValidException;
import com.woobeee.blog.post.exception.PostUpdateFromNotValidException;
import com.woobeee.blog.post.service.CategoryService;
import com.woobeee.blog.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public Response<Void> savePost(
            @Valid @RequestBody PostCreateRequest postCreateRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new PostCreateFromNotValidException("게시글 생성 폼이 유효하지 않습니다.");
        }
        postService.create(postCreateRequest);

        return Response.success();
    }

    @GetMapping("/posts/{postId}")
    public Response<PostReadResponse> readPost(
            @PathVariable Long postId
    ) {
        Post post = postService.read(postId);

        PostReadResponse response = PostReadResponse.builder()
                .context(post.getContext())
                .title(post.getTitle())
                .status(post.getStatus())
                .count(post.getCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .build();

        return Response.success(response);
    }

    @PutMapping("/posts/{postId}")
    public Response<Void> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new PostUpdateFromNotValidException("게시글 생성 폼이 유효하지 않습니다.");
        }

        postService.update(postUpdateRequest);

        return Response.success();
    }

    @DeleteMapping("/posts/{postId}")
    public Response<Void> deletePost(
            @PathVariable Long postId
    ) {
        postService.delete(postId);

        return Response.deleteSuccess();
    }

    @GetMapping("/categories/{categoryId}/posts")
    public Response<List<PostReadResponse>> readCategoryPost(
            @PathVariable Long categoryId
    ) {
        List<PostReadResponse> responses = new ArrayList<>();
        List<Post> posts = postService.readCategoryPosts(categoryId);

        for (Post post : posts) {
            PostReadResponse response = PostReadResponse.builder()
                    .context(post.getContext())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .count(post.getCount())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .deletedAt(post.getDeletedAt())
                    .build();

            responses.add(response);
        }

        return Response.success(responses);
    }

    @GetMapping("/posts")
    public Response<List<PostReadResponse>> readAllPost() {
        List<PostReadResponse> responses = new ArrayList<>();
        List<Post> posts = postService.readAll();

        for (Post post : posts) {
            PostReadResponse response = PostReadResponse.builder()
                    .context(post.getContext())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .count(post.getCount())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .deletedAt(post.getDeletedAt())
                    .build();

            responses.add(response);
        }

        return Response.success(responses);
    }

    @GetMapping("/tags/{tagName}/posts")
    public Response<List<PostReadResponse>> readTagPost(
            @PathVariable String tagName
    ) {
        List<Post> posts = postService.readTagPosts(tagName);
        List<PostReadResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            PostReadResponse response = PostReadResponse.builder()
                    .context(post.getContext())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .count(post.getCount())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .deletedAt(post.getDeletedAt())
                    .build();

            responses.add(response);
        }
        return Response.success(responses);
    }
}
