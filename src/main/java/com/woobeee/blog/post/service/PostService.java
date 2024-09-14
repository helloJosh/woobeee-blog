package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.PostCreateRequest;
import com.woobeee.blog.post.dto.PostUpdateRequest;
import com.woobeee.blog.post.entity.Post;

import java.util.List;

/**
 * 게시글 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface PostService {

    /**
     * 게시글 생성 요청.
     *
     * @param postCreateRequest 게시글 생성 요청 폼
     */
    void create(PostCreateRequest postCreateRequest);

    /**
     * 게시글 삭제 요청.
     *
     * @param postId 게시글 아이디
     */
    void delete(Long postId);

    /**
     * 게시글 수정 요청.
     *
     * @param postUpdateRequest 게시글 수정 요청 폼
     */
    void update(PostUpdateRequest postUpdateRequest);

    /**
     * 게시글 조회 요청.
     *
     * @param postId 게시글 아이디
     * @return 게시글
     */
    Post read(Long postId);

    /**
     * 게시글 전체 조회.
     *
     * @return 전체 게시글
     */
    List<Post> readAll();

    /**
     * 카테고리별 게시글 조회 요청.
     *
     * @param categoryId 카테고리 아이디
     * @return 카테고리별 게시글 리스트
     */
    List<Post> readCategoryPosts(Long categoryId);

    /**
     * 태그별 게시글 조회 요청.
     *
     * @param tag 태그 이름
     * @return 태그별 게시글 리스트
     */
    List<Post> readTagPosts(String tag);
}
