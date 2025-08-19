package com.woobeee.auth.service;

import com.woobeee.auth.dto.PostLoginRequest;
import com.woobeee.auth.dto.PostSignInRequest;
import com.woobeee.auth.entity.UserCredential;

/**
 * User Auth Service layer
 */
public interface UserCredentialService {
    String logIn(PostLoginRequest loginRequest);
    String signIn(PostSignInRequest postSignInRequest);

    /// Later
    void signOut();

}
