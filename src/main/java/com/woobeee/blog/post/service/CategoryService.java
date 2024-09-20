package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.request.CategoryCreateRequest;
import com.woobeee.blog.post.dto.request.CategoryUpdateRequest;
import com.woobeee.blog.post.dto.response.CategoryReadAllResponse;
import com.woobeee.blog.post.entity.Category;

/**
 * 카테고리 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface CategoryService {
    /**
     * 카테고리 생성 메소드.
     *
     * @param categoryCreateRequest 카테고리 생성 요청 폼
     */
    void create(CategoryCreateRequest categoryCreateRequest);

    /**
     * 카테고리 삭제 메소드.
     *
     * @param categoryId 카테고리아이디
     */
    void delete(Long categoryId);

    /**
     * 카테고리 수정 메소드.
     *
     * @param categoryUpdateRequest 카테고리 수정 요청 폼
     */
    void update(CategoryUpdateRequest categoryUpdateRequest);

    /**
     * 카테고리 조회 메소드.
     *
     * @param categoryId 카테고리 아이디
     * @return 카테고리
     */
    Category read(Long categoryId);


    /**
     * 카테고리 조회 전체 메소드.
     *
     * @return 카테고리
     */
    CategoryReadAllResponse readAll();
}
