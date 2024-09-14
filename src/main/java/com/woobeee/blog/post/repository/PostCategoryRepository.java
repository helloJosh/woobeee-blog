package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Category;
import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 카테고리 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    Optional<PostCategory> findPostCategoryByCategoryAndPost(Category category, Post post);
    List<PostCategory> findAllByCategory(Category category);
}
