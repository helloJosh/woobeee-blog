package com.woobeee.auth.service;

import com.woobeee.auth.dto.PostLoginRequest;
import com.woobeee.auth.dto.PostSignInRequest;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.PasswordNotMatchException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.repository.UserCredentialRepository;
import com.woobeee.auth.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class UserCredentialServiceImpl implements UserCredentialService{
    private final UserCredentialRepository userCredentialRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String logIn(PostLoginRequest loginRequest) {
        String retToken;

        UserCredential userCredential = userCredentialRepository
                .findUserCredentialByLoginId(loginRequest.getLoginId())
                .orElseThrow(
                        () -> new UserNotFoundException("login.userNotFound"));

        if ( passwordEncoder.matches(
                userCredential.getPassword(),
                loginRequest.getPassword())
        ) {
            List<AuthType> authTypes = userCredential.getUserAuths()
                    .stream()
                    .map(userAuth -> userAuth.getAuth().getAuthType())
                    .toList();

            String loginId = userCredential.getLoginId();

            retToken = jwtTokenProvider.generateToken(authTypes, loginId);

        } else {
            throw new PasswordNotMatchException("");
        }

        return retToken;
    }

    @Override
    public String signIn(PostSignInRequest postSignInRequest) {
        return "";
    }

    @Override
    public void signOut() {

    }
}
