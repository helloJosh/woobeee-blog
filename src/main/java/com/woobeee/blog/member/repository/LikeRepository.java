package com.woobeee.blog.member.repository;

import com.woobeee.blog.member.entity.Like;
import com.woobeee.blog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 좋아요 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface LikeRepository extends JpaRepository<Like, Long> {
}
