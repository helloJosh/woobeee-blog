package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.PostTag;
import com.woobeee.blog.post.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 게시글 태그 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    Optional<PostTag> findPostTagByPostAndTag(Post post, Tag tag);
}
