package com.woobeee.blog.auth.impl;

import com.woobeee.blog.auth.exception.AuthenticationInvalidException;
import com.woobeee.blog.auth.service.AuthenticationService;
import com.woobeee.blog.auth.util.JwtUtil;
import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.member.exception.LoginIdNotFoundException;
import com.woobeee.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> login(String loginId, String password) throws AuthenticationInvalidException {
        Map<String, String> token = new HashMap<>();

        Member member = memberRepository
                .findMemberByLoginId(loginId)
                .orElseThrow(
                        ()-> new LoginIdNotFoundException(loginId + "가 존재 하지 않습니다.")
                );

        log.trace("DB저장 비밀번호 : {}", member.getPassword());
        log.trace("입력된 비밀번호 : {}", password);
        log.trace("입력된 비밀번호 인코딩 후 : {}", passwordEncoder.encode(password));
        if (passwordEncoder.matches(password, member.getPassword())) {
            token.put("AccessToken", jwtUtil.generateAccessToken(loginId));
            token.put("RefreshToken", jwtUtil.generateRefreshToken(loginId));
        } else {
            throw new AuthenticationInvalidException("비밀번호가 틀렸습니다.");
        }

        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateToken(String accessToken, String refreshToken) {
        accessToken = jwtUtil.validateAccessToken(accessToken, refreshToken);
        return accessToken;
    }
}
