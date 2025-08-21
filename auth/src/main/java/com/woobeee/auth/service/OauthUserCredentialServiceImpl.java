package com.woobeee.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.UserConflictException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.jwt.JwtTokenProvider;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public String signIn(String idTokenString) {
        GoogleIdToken idToken = verifyIdToken(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("signIn.googleTokenNotValid");
        }

        String email = idToken.getPayload().getEmail();
        String userUuid = idToken.getPayload().getSubject();

        if (userCredentialRepository.existsByLoginId(email)) {
            throw new UserConflictException("signIn.userConflict");
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
                        .auth(auth)
                        .userCredential(savedUserCredential)
                        .id(new UserAuth.UserAuthId(savedUserCredential.getId(), auth.getId()))
                        .build())
                .toList();

        userAuthRepository.saveAll(userAuths);

        return jwtTokenProvider.generateToken(
                List.of(AuthType.ROLE_MEMBER),
                email
        );
    }

    @Override
    public String logIn(String idTokenString) {
        GoogleIdToken idToken = verifyIdToken(idTokenString);

        if (idToken == null) {
            throw new RuntimeException("signIn.googleTokenNotValid");
        }

        String email = idToken.getPayload().getEmail();
        String userUuid = idToken.getPayload().getSubject();

        UserCredential user = userCredentialRepository.findUserCredentialByLoginId(email)
                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 사용자입니다."));

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
