package com.woobeee.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.woobeee.auth.dto.provider.MessageEvent;
import com.woobeee.auth.dto.request.PostOauthSignUpRequest;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.UserConflictException;
import com.woobeee.auth.exception.UserNotFoundException;
import com.woobeee.auth.jwt.JwtTokenProvider;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OauthUserCredentialServiceImplTest {

    @Mock
    private GoogleIdTokenVerifier verifier;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private UserCredentialRepository userCredentialRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OauthUserCredentialServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new OauthUserCredentialServiceImpl(
                verifier,
                authRepository,
                userAuthRepository,
                userCredentialRepository,
                jwtTokenProvider,
                passwordEncoder,
                eventPublisher,
                objectMapper
        ));
    }

    @Test
    void logIn_whenUserNotFound_thenThrow() {
        GoogleIdToken idToken = org.mockito.Mockito.mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = org.mockito.Mockito.mock(GoogleIdToken.Payload.class);

        doReturn(idToken).when(service).verifyIdToken("token");
        given(idToken.getPayload()).willReturn(payload);
        given(payload.getEmail()).willReturn("nouser@test.com");
        given(userCredentialRepository.findUserCredentialByLoginId("nouser@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.logIn("token"))
                .isInstanceOf(UserNotFoundException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void signIn_whenAlreadyExists_thenThrowConflict() {
        GoogleIdToken idToken = org.mockito.Mockito.mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = org.mockito.Mockito.mock(GoogleIdToken.Payload.class);

        doReturn(idToken).when(service).verifyIdToken("token");
        given(idToken.getPayload()).willReturn(payload);
        given(payload.getEmail()).willReturn("exists@test.com");
        given(userCredentialRepository.existsByLoginId("exists@test.com")).willReturn(true);

        assertThatThrownBy(() -> service.signIn("token"))
                .isInstanceOf(UserConflictException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void signUp_success_thenPublishSignUpTrigger() {
        UUID userId = UUID.randomUUID();

        GoogleIdToken idToken = org.mockito.Mockito.mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = org.mockito.Mockito.mock(GoogleIdToken.Payload.class);

        PostOauthSignUpRequest request = new PostOauthSignUpRequest(
                "token",
                "nick",
                "insta",
                "seoul",
                "intro",
                "ideal",
                3,
                List.of(),
                null,
                null,
                null
        );

        Auth memberAuth = Auth.builder().id(1L).authType(AuthType.ROLE_MEMBER).build();
        UserCredential saved = UserCredential.builder()
                .id(userId)
                .loginId("new@test.com")
                .password("enc")
                .build();

        doReturn(idToken).when(service).verifyIdToken("token");
        given(idToken.getPayload()).willReturn(payload);
        given(payload.getEmail()).willReturn("new@test.com");
        given(payload.getSubject()).willReturn("google-sub");
        given(userCredentialRepository.existsByLoginId("new@test.com")).willReturn(false);
        given(authRepository.findAllByAuthTypeIn(List.of(AuthType.ROLE_MEMBER))).willReturn(List.of(memberAuth));
        given(passwordEncoder.encode("google-sub")).willReturn("enc");
        given(userCredentialRepository.save(any(UserCredential.class))).willReturn(saved);
        given(jwtTokenProvider.generateToken(List.of(AuthType.ROLE_MEMBER), "new@test.com")).willReturn("jwt-token");

        String token = service.signUp(request);

        assertThat(token).isEqualTo("jwt-token");

        ArgumentCaptor<MessageEvent> eventCaptor = ArgumentCaptor.forClass(MessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        MessageEvent event = eventCaptor.getValue();
        assertThat(event.topic()).isEqualTo("sign-up-trigger");
        assertThat(event.key()).isEqualTo("new@test.com");

        ObjectNode node = (ObjectNode) event.message();
        assertThat(node.get("id").asText()).isEqualTo(userId.toString());
        assertThat(node.get("loginId").asText()).isEqualTo("new@test.com");
        assertThat(node.get("nickname").asText()).isEqualTo("nick");
    }
}
