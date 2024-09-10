package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 카테고리 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface CategoryRepository extends JpaRepository<Long, Category> {
}
