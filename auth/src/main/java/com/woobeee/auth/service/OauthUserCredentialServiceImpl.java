package com.woobeee.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class OauthUserCredentialServiceImpl implements OauthUserCredentialService{
    @Override
    public String signIn() {
        return "";
    }

    @Override
    public String logIn() {
        return "";
    }
}
