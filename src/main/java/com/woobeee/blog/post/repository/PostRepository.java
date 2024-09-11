package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게시글 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
