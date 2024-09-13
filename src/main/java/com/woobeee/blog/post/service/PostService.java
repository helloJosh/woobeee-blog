package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;

/**
 * 게시글 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PostService {
    void create(PostCreateRequest postCreateRequest);
    Long delete(Long postId);
    Long update(PostUpdateRequest postUpdateRequest);
    Long read(Long postId);
}
