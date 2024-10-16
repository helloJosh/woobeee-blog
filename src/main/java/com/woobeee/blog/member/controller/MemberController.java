package com.woobeee.blog.member.controller;

import com.woobeee.blog.api.Response;
import com.woobeee.blog.member.dto.LoginRequest;
import com.woobeee.blog.member.dto.LoginResponse;
import com.woobeee.blog.member.dto.MemberRequest;
import com.woobeee.blog.member.dto.MemberResponse;
import com.woobeee.blog.member.exception.LoginRequestFormException;
import com.woobeee.blog.member.exception.MemberRequestFormException;
import com.woobeee.blog.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 회원컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입
     *
     * @param memberRequest 회원생성요청
     * @return Response
     */
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Void> createMember(@RequestBody @Valid MemberRequest memberRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MemberRequestFormException(bindingResult.getFieldError().toString());
        }

        memberService.signIn(memberRequest);

        return Response.createSuccess();
    }

    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청 데이터
     * @return JWT 토큰이 포함된 Response
     */
    @PostMapping("/members/login")
    public Response<LoginResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new LoginRequestFormException(bindingResult.getFieldError().toString());
        }

        return Response.success(memberService.login(loginRequest));
    }

}
