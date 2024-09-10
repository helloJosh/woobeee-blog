package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게시글 태그 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface PostTagRepository extends JpaRepository<Long, PostTag> {
}
