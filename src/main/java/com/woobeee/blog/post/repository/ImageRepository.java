package com.woobeee.blog.post.repository;

import com.woobeee.blog.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 이미지 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
