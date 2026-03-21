package com.woobeee.auth.service;

import com.woobeee.auth.dto.response.IssuedAuthTokens;
import com.woobeee.auth.dto.request.PostOauthSignUpRequest;

public interface OauthUserCredentialService {
    IssuedAuthTokens signIn(String idTokenString);
    IssuedAuthTokens signUp(PostOauthSignUpRequest request);
    IssuedAuthTokens logIn(String idTokenString);
    IssuedAuthTokens refresh(String refreshToken);
    void logout(String refreshToken);
}
