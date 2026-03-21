package com.woobeee.auth.service;

import com.woobeee.auth.dto.request.PostLoginRequest;
import com.woobeee.auth.dto.request.PostSignInRequest;
import com.woobeee.auth.dto.response.IssuedAuthTokens;

/**
 * User Auth Service layer
 */
public interface UserCredentialService {
    IssuedAuthTokens logIn(PostLoginRequest loginRequest);
    IssuedAuthTokens signIn(PostSignInRequest postSignInRequest);

    /// Later
    void signOut(String refreshToken);

}
