package com.woobeee.auth.config;

import com.woobeee.auth.entity.Auth;
import com.woobeee.auth.entity.enums.AuthType;
import com.woobeee.auth.repository.AuthRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializeConfig {
    private final AuthRepository authRepository;

    @PostConstruct
    public void initAuth() {
        if (!authRepository.existsByAuthType(AuthType.ROLE_GUEST)) {
            Auth guest = Auth.builder().authType(AuthType.ROLE_GUEST).build();
            authRepository.save(guest);
        }

        if (!authRepository.existsByAuthType(AuthType.ROLE_MEMBER)) {
            Auth member = Auth.builder().authType(AuthType.ROLE_MEMBER).build();
            authRepository.save(member);
        }

        if (!authRepository.existsByAuthType(AuthType.ROLE_ADMIN)) {
            Auth admin = Auth.builder().authType(AuthType.ROLE_ADMIN).build();
            authRepository.save(admin);
        }
    }
}
