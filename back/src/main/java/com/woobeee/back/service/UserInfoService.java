package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostSignUpRequest;

public interface UserInfoService {
    @Deprecated
    void signIn(String id, String loginId);
    void signUp(PostSignUpRequest request);
}
