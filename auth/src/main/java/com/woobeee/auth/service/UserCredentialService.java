package com.woobeee.auth.service;

import com.woobeee.auth.dto.request.PostLoginRequest;
import com.woobeee.auth.dto.request.PostSignInRequest;

/**
 * User Auth Service layer
 */
public interface UserCredentialService {
    String logIn(PostLoginRequest loginRequest);
    String signIn(PostSignInRequest postSignInRequest);

    /// Later
    void signOut();

}
