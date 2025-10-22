package com.woobeee.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.woobeee.auth.dto.request.PostLoginRequest;
import com.woobeee.auth.dto.request.PostSignInRequest;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.ErrorCode;
import com.woobeee.auth.exception.PasswordNotMatchException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.provider.MessageEvent;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import com.woobeee.auth.jwt.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class UserCredentialServiceImpl implements UserCredentialService{
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final AuthRepository authRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserCredentialRepository userCredentialRepository;

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public String logIn(PostLoginRequest loginRequest) {
        String retToken;

        UserCredential userCredential = userCredentialRepository
                .findUserCredentialByLoginId(loginRequest.loginId())
                .orElseThrow(
                        () -> new UserNotFoundException(ErrorCode.login_userNotFound));

        if ( passwordEncoder.matches(
                userCredential.getPassword(),
                loginRequest.password())
        ) {
            List<Long> userAuths = userAuthRepository
                    .findAllById_UserId(userCredential.getId())
                    .stream()
                    .map(UserAuth::getAuthId)
                    .toList();

            List<AuthType> authTypes = authRepository
                    .findAllById(userAuths)
                    .stream()
                    .map(Auth::getAuthType)
                    .toList();

            String loginId = userCredential.getLoginId();

            retToken = jwtTokenProvider.generateToken(authTypes, loginId);

            return retToken;
        } else {
            throw new PasswordNotMatchException(ErrorCode.login_passwordNotMatch);
        }
    }

    @Override
    public String signIn(PostSignInRequest postSignInRequest) {
        List<Auth> auths = authRepository
                .findAllByAuthTypeIn(postSignInRequest.authTypes());

        UserCredential userCredential = UserCredential.builder()
                .loginId(postSignInRequest.loginId())
                .password(postSignInRequest.password())
                .build();

        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);

        List<UserAuth> userAuths = auths.stream()
                .map(auth -> UserAuth.builder()
                        .id(new UserAuth.UserAuthId(savedUserCredential.getId(), auth.getId()))
                        .build())
                .toList();

        userAuthRepository.saveAll(userAuths);

        String retToken = jwtTokenProvider.generateToken(
                postSignInRequest.authTypes(), postSignInRequest.loginId());

        ObjectNode node = objectMapper.createObjectNode();
        node.put("loginId", postSignInRequest.loginId());
        node.put("nickname", postSignInRequest.nickname());

        eventPublisher.publishEvent(new MessageEvent(node));
        return retToken;
    }

    @Override
    public void signOut() {

    }

}
