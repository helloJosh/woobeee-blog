package com.woobeee.blog.auth.repository.impl;

import com.woobeee.blog.auth.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 토큰 저장소 구현체.
 *
 * @author 김병우
 */
@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final Map<String, String> accessTokenMap = new HashMap<>();
    private final Map<String, String> refreshTokenMap = new HashMap<>();


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAccessToken(String accessToken, String loginId) {
        accessTokenMap.put(loginId, accessToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveRefreshToken(String refreshToken, String loginId) {
        refreshTokenMap.put(loginId, refreshToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findAccessToken(String loginId) {
        return accessTokenMap.get(loginId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findRefreshToken(String loginId) {
        return refreshTokenMap.get(loginId);
    }
}
