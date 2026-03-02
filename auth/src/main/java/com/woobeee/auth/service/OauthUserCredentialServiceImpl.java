package com.woobeee.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.woobeee.auth.dto.provider.MessageEvent;
import com.woobeee.auth.dto.request.PostOauthSignUpRequest;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.ErrorCode;
import com.woobeee.auth.exception.UserConflictException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.jwt.JwtTokenProvider;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OauthUserCredentialServiceImpl implements OauthUserCredentialService{

    private final GoogleIdTokenVerifier verifier;
    private final AuthRepository authRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public String signIn(String idTokenString) {
        GoogleIdToken idToken = verifyIdToken(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("signIn.googleTokenNotValid");
        }

        String email = idToken.getPayload().getEmail();
        String userUuid = idToken.getPayload().getSubject();

        if (userCredentialRepository.existsByLoginId(email)) {
            throw new UserConflictException(ErrorCode.signIn_userConflict);
        }

        List<Auth> auths = authRepository
                .findAllByAuthTypeIn(
                        List.of(AuthType.ROLE_MEMBER)
                );

        UserCredential userCredential = UserCredential.builder()
                .loginId(email)
                .password(passwordEncoder.encode(userUuid))
                .build();

        userCredential = userCredentialRepository.save(userCredential);

        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);

        List<UserAuth> userAuths = auths.stream()
                .map(auth -> UserAuth.builder()
                        .id(new UserAuth.UserAuthId(savedUserCredential.getId(), auth.getId()))
                        .build())
                .toList();

        userAuthRepository.saveAll(userAuths);

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("id",
                String.valueOf(savedUserCredential.getId()));
        payload.put("loginId",
                savedUserCredential.getLoginId());


        MessageEvent event = MessageEvent.builder()
                .eventId(UUID.randomUUID())
                .topic("sign-in-trigger")
                .key(savedUserCredential.getLoginId())
                .message(payload)
                .build();

        eventPublisher.publishEvent(event);

        return jwtTokenProvider.generateToken(
                List.of(AuthType.ROLE_MEMBER),
                email
        );
    }

    @Override
    public String signUp(PostOauthSignUpRequest request) {
        GoogleIdToken idToken = verifyIdToken(request.idToken());
        if (idToken == null) {
            throw new RuntimeException("signIn.googleTokenNotValid");
        }

        String email = idToken.getPayload().getEmail();
        String userUuid = idToken.getPayload().getSubject();

        if (userCredentialRepository.existsByLoginId(email)) {
            throw new UserConflictException(ErrorCode.signIn_userConflict);
        }

        List<Auth> auths = authRepository.findAllByAuthTypeIn(List.of(AuthType.ROLE_MEMBER));

        UserCredential userCredential = UserCredential.builder()
                .loginId(email)
                .password(passwordEncoder.encode(userUuid))
                .build();

        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);

        List<UserAuth> userAuths = auths.stream()
                .map(auth -> UserAuth.builder()
                        .id(new UserAuth.UserAuthId(savedUserCredential.getId(), auth.getId()))
                        .build())
                .toList();
        userAuthRepository.saveAll(userAuths);

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("id", String.valueOf(savedUserCredential.getId()));
        payload.put("loginId", savedUserCredential.getLoginId());
        payload.put("nickname", request.nickname());
        payload.put("instargramId", request.instargramId());
        payload.put("preferredRegion", request.preferredRegion());
        payload.put("introText", request.introText());
        payload.put("idealTypeText", request.idealTypeText());
        if (request.yearsOfTraining() != null) {
            payload.put("yearsOfTraining", request.yearsOfTraining());
        } else {
            payload.putNull("yearsOfTraining");
        }

        payload.set("tagIds", objectMapper.valueToTree(request.tagIds()));
        payload.set("inbodyRecord", objectMapper.valueToTree(request.inbodyRecord()));
        payload.set("strengthRecord", objectMapper.valueToTree(request.strengthRecord()));
        payload.set("runningRecord", objectMapper.valueToTree(request.runningRecord()));

        MessageEvent event = MessageEvent.builder()
                .eventId(UUID.randomUUID())
                .topic("sign-up-trigger")
                .key(savedUserCredential.getLoginId())
                .message(payload)
                .build();
        eventPublisher.publishEvent(event);

        return jwtTokenProvider.generateToken(List.of(AuthType.ROLE_MEMBER), email);
    }

    @Override
    public String logIn(String idTokenString) {
        GoogleIdToken idToken = verifyIdToken(idTokenString);

        if (idToken == null) {
            throw new RuntimeException("signIn.googleTokenNotValid");
        }

        String email = idToken.getPayload().getEmail();
        userCredentialRepository.findUserCredentialByLoginId(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.login_userNotFound));

        return jwtTokenProvider.generateToken(
                List.of(AuthType.ROLE_MEMBER),
                email
        );
    }


    public GoogleIdToken verifyIdToken(String idTokenString) {
        try {
            return verifier.verify(idTokenString);
        } catch (Exception e) {
            return null;
        }
    }
}
