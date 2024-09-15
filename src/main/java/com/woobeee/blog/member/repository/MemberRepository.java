package com.woobeee.blog.member.repository;

import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 저장소 접근 클래스.
 *
 * @author 김병우
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsMemberByLoginId(String loginId);
    Optional<Member> findMemberByLoginId(String loginId);
}
