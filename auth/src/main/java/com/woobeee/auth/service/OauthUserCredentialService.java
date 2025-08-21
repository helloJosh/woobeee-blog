package com.woobeee.auth.service;

public interface OauthUserCredentialService {
    String signIn(String idTokenString);
    String logIn(String idTokenString);
}
