package com.woobeee.blog.member.service;

import com.woobeee.blog.member.dto.LoginRequest;
import com.woobeee.blog.member.dto.LoginResponse;
import com.woobeee.blog.member.dto.MemberRequest;
import com.woobeee.blog.member.dto.MemberResponse;
import com.woobeee.blog.member.exception.DuplicatedLoginIdException;

/**
 * 맴버 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface MemberService {
    /**
     * 회원가입.
     *
     * @param memberRequest 맴버 생성 요청
     * @exception DuplicatedLoginIdException 로그인아이디 중복시 에러반환
     */
    void signIn(MemberRequest memberRequest) throws DuplicatedLoginIdException;

    /**
     * 맴버 읽기.
     *
     * @param loginId 로그인 아이디
     * @return 맴버 응답 레코드
     */
    MemberResponse findMember(String loginId);


    /**
     * 맴버 읽기.
     *
     * @param loginRequest 로그인 아이디
     * @return 맴버 응답 레코드
     */
    LoginResponse login(LoginRequest loginRequest);
}
