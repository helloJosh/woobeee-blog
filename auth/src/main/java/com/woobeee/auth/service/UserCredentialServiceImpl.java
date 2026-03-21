package com.woobeee.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.woobeee.auth.dto.provider.MessageEvent;
import com.woobeee.auth.dto.request.PostLoginRequest;
import com.woobeee.auth.dto.request.PostSignInRequest;
import com.woobeee.auth.dto.response.IssuedAuthTokens;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.ErrorCode;
import com.woobeee.auth.exception.PasswordNotMatchException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class UserCredentialServiceImpl implements UserCredentialService {
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    private final AuthRepository authRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserCredentialRepository userCredentialRepository;

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IssuedAuthTokens logIn(PostLoginRequest loginRequest) {
        UserCredential userCredential = userCredentialRepository
                .findUserCredentialByLoginId(loginRequest.loginId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.login_userNotFound));

        if (!passwordEncoder.matches(loginRequest.password(), userCredential.getPassword())) {
            throw new PasswordNotMatchException(ErrorCode.login_passwordNotMatch);
        }

        List<Long> userAuths = userAuthRepository.findAllById_UserId(userCredential.getId())
                .stream()
                .map(UserAuth::getAuthId)
                .toList();

        List<AuthType> authTypes = authRepository.findAllById(userAuths)
                .stream()
                .map(Auth::getAuthType)
                .toList();

        return authTokenService.issueTokens(userCredential.getLoginId(), authTypes);
    }

    @Override
    public IssuedAuthTokens signIn(PostSignInRequest postSignInRequest) {
        List<Auth> auths = authRepository.findAllByAuthTypeIn(postSignInRequest.authTypes());

        UserCredential userCredential = UserCredential.builder()
                .loginId(postSignInRequest.loginId())
                .password(passwordEncoder.encode(postSignInRequest.password()))
                .build();

        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);

        List<UserAuth> userAuths = auths.stream()
                .map(auth -> UserAuth.builder()
                        .id(new UserAuth.UserAuthId(savedUserCredential.getId(), auth.getId()))
                        .build())
                .toList();

        userAuthRepository.saveAll(userAuths);

        IssuedAuthTokens issuedAuthTokens = authTokenService.issueTokens(
                postSignInRequest.loginId(),
                postSignInRequest.authTypes()
        );

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", savedUserCredential.getId().toString());
        node.put("loginId", postSignInRequest.loginId());

        MessageEvent event = MessageEvent.builder()
                .eventId(UUID.randomUUID())
                .topic("sign-in-trigger")
                .key(postSignInRequest.loginId())
                .message(node)
                .build();

        eventPublisher.publishEvent(event);
        return issuedAuthTokens;
    }

    @Override
    public void signOut(String refreshToken) {
        authTokenService.revoke(refreshToken);
    }
}
