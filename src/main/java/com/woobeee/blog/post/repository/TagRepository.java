package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 태그 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsTagByName(String name);
    Optional<Tag> findTagByName(String name);
}
