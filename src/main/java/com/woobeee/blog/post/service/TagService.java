package com.woobeee.blog.post.service;

import com.woobeee.blog.post.dto.TagCreateRequest;
import com.woobeee.blog.post.dto.TagUpdateRequest;
import com.woobeee.blog.post.entity.Tag;

/**
 * 태그 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface TagService {
    /**
     * 태그 생성 메소드.
     *
     * @param tagCreateRequest 태그생성요청폼
     */
    void create(TagCreateRequest tagCreateRequest);

    /**
     * 태그 삭제 메소드.
     *
     * @param tagId 태그아이디
     */
    void delete(Long tagId);

    /**
     * 태그 수정 메소드.
     *
     * @param tagUpdateRequest 태그수정요청폼
     */
    void update(TagUpdateRequest tagUpdateRequest);

    /**
     * 태그 조회 메소드.
     *
     * @param tagId 태그아이디
     * @return 태그
     */
    Tag read(Long tagId);
}
