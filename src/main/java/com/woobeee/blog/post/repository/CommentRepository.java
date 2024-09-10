package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 댓글 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface CommentRepository extends JpaRepository<Long, Comment> {
}
