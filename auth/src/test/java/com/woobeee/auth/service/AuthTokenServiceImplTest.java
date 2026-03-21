package com.woobeee.auth.service;

import com.woobeee.auth.dto.response.IssuedAuthTokens;
import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.UserAuth;
import com.woobeee.auth.entity.UserCredential;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.exception.JwtNotValidException;
import com.woobeee.auth.jwt.JwtTokenProvider;
import com.woobeee.auth.repository.AuthRepository;
import com.woobeee.auth.repository.UserAuthRepository;
import com.woobeee.auth.repository.UserCredentialRepository;
import com.woobeee.auth.store.RefreshTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;
    @Mock
    private UserCredentialRepository userCredentialRepository;
    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private AuthRepository authRepository;

    private AuthTokenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthTokenServiceImpl(
                jwtTokenProvider,
                refreshTokenStore,
                userCredentialRepository,
                userAuthRepository,
                authRepository
        );
    }

    @Test
    void issueTokens_savesRefreshTokenHash() {
        given(jwtTokenProvider.generateAccessToken(List.of(AuthType.ROLE_MEMBER), "member@test.com"))
                .willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(any(String.class), any(UUID.class)))
                .willReturn("refresh-token");
        given(jwtTokenProvider.getAccessTokenExpirationMillis()).willReturn(3600000L);
        given(jwtTokenProvider.getRefreshTokenExpirationMillis()).willReturn(86400000L);

        IssuedAuthTokens tokens = service.issueTokens("member@test.com", List.of(AuthType.ROLE_MEMBER));

        assertThat(tokens.response().accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<String> loginIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> tokenHashCaptor = ArgumentCaptor.forClass(String.class);
        verify(refreshTokenStore).save(any(UUID.class), loginIdCaptor.capture(), tokenHashCaptor.capture(), any());
        assertThat(loginIdCaptor.getValue()).isEqualTo("member@test.com");
        assertThat(tokenHashCaptor.getValue()).isNotBlank();
    }

    @Test
    void refresh_rotatesRefreshToken() {
        UUID refreshTokenId = UUID.randomUUID();
        UserCredential userCredential = UserCredential.builder()
                .id(UUID.randomUUID())
                .loginId("member@test.com")
                .password("encoded")
                .build();

        given(jwtTokenProvider.parseRefreshToken("old-refresh"))
                .willReturn(new JwtTokenProvider.RefreshTokenPayload("member@test.com", refreshTokenId));
        given(refreshTokenStore.find(refreshTokenId))
                .willReturn(Optional.of(new RefreshTokenStore.StoredRefreshToken(
                        "member@test.com",
                        "93dd6d39cf0a73f62182459fbcbbd0dfaa9974b15193ad5b609fe181f7b0c6fa"
                )));
        given(userCredentialRepository.findUserCredentialByLoginId("member@test.com"))
                .willReturn(Optional.of(userCredential));
        given(userAuthRepository.findAllById_UserId(userCredential.getId()))
                .willReturn(List.of(UserAuth.builder()
                        .id(new UserAuth.UserAuthId(userCredential.getId(), 1L))
                        .build()));
        given(authRepository.findAllById(List.of(1L)))
                .willReturn(List.of(Auth.builder().id(1L).authType(AuthType.ROLE_MEMBER).build()));
        given(jwtTokenProvider.generateAccessToken(List.of(AuthType.ROLE_MEMBER), "member@test.com"))
                .willReturn("new-access");
        given(jwtTokenProvider.generateRefreshToken(any(String.class), any(UUID.class)))
                .willReturn("new-refresh");
        given(jwtTokenProvider.getAccessTokenExpirationMillis()).willReturn(3600000L);
        given(jwtTokenProvider.getRefreshTokenExpirationMillis()).willReturn(86400000L);

        IssuedAuthTokens tokens = service.refresh("old-refresh");

        assertThat(tokens.response().accessToken()).isEqualTo("new-access");
        assertThat(tokens.refreshToken()).isEqualTo("new-refresh");
        InOrder inOrder = inOrder(refreshTokenStore);
        inOrder.verify(refreshTokenStore).delete(refreshTokenId);
        inOrder.verify(refreshTokenStore).save(any(UUID.class), any(String.class), any(String.class), any());
    }

    @Test
    void refresh_whenStoredTokenMissing_thenThrow() {
        UUID refreshTokenId = UUID.randomUUID();
        given(jwtTokenProvider.parseRefreshToken("missing-refresh"))
                .willReturn(new JwtTokenProvider.RefreshTokenPayload("member@test.com", refreshTokenId));
        given(refreshTokenStore.find(refreshTokenId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh("missing-refresh"))
                .isInstanceOf(JwtNotValidException.class);
    }
}
