package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsCategoryByName(String name);
    Optional<Category> findCategoryByName(String name);
    List<Category> findCategoriesByParentIsNull();

}
