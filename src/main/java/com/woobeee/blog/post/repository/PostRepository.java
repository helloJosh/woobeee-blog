package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.PostCategory;
import com.woobeee.blog.post.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시글 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPostCategories(List<PostCategory> postCategories);
    List<Post> findAllByPostTags(List<PostTag> postTags);
}
