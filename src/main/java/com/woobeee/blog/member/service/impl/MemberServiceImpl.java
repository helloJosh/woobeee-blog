package com.woobeee.blog.member.service.impl;

import com.woobeee.blog.member.dto.MemberRequest;
import com.woobeee.blog.member.dto.MemberResponse;
import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.member.exception.DuplicatedLoginIdException;
import com.woobeee.blog.member.exception.MemberNotFoundException;
import com.woobeee.blog.member.repository.MemberRepository;
import com.woobeee.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 맴버 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public void signIn(MemberRequest memberRequest) {
        if (memberRepository.existsMemberByLoginId(memberRequest.loginId())) {
            throw new DuplicatedLoginIdException(memberRequest.loginId() + ": 중복 에러");
        }

        Member member = Member.builder()
                .loginId(memberRequest.loginId())
                .password(passwordEncoder.encode(memberRequest.password()))
                .name(memberRequest.name())
                .email(memberRequest.email())
                .status(memberRequest.status())
                .auth(memberRequest.auth())
                .build();

        memberRepository.save(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberResponse findMember(String loginId) {
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(()-> new MemberNotFoundException(loginId + "에 해당하는 맴버가 없습니다."));

        return MemberResponse.builder()
                .loginId(member.getLoginId())
                .password(passwordEncoder.encode(member.getPassword()))
                .name(member.getName())
                .email(member.getEmail())
                .status(member.getStatus())
                .auth(member.getAuth())
                .build();
    }
}
