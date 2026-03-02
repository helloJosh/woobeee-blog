package com.woobeee.auth.service;

import com.woobeee.auth.dto.request.PostOauthSignUpRequest;

public interface OauthUserCredentialService {
    String signIn(String idTokenString);
    String signUp(PostOauthSignUpRequest request);
    String logIn(String idTokenString);
}
