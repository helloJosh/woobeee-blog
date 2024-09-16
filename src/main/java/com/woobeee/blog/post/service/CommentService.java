package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.request.CommentCreateRequest;
import com.woobeee.blog.post.dto.request.CommentUpdateRequest;
import com.woobeee.blog.post.dto.response.CommentReadAllResponse;

/**
 * 댓글 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface CommentService {
    /**
     * 댓글 생성 메소드.
     *
     * @param commentCreateRequest 댓글 생성 요청 폼
     */
    void create(CommentCreateRequest commentCreateRequest);

    /**
     * 댓글 삭제 메소드.
     *
     * @param commentId 댓글 아이디
     */
    void delete(Long commentId);

    /**
     * 댓글 수정 메소드.
     *
     * @param commentUpdateRequest 댓글 수정 요청 폼
     */
    void update(CommentUpdateRequest commentUpdateRequest);

    /**
     * 게시글 댓글 전체 조회 메소드.
     *
     * @param postId 게시글 아이디
     * @return 댓글
     */
    CommentReadAllResponse readAll(Long postId);
}
