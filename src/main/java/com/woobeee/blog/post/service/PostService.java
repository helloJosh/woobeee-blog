package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.entity.Post;

/**
 * 게시글 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PostService {
    void create(PostCreateRequest postCreateRequest);
    void delete(Long postId);
    void update(PostUpdateRequest postUpdateRequest);
    Post read(Long postId);
}
