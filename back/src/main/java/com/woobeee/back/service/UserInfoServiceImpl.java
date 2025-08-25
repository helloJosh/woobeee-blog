package com.woobeee.back.service;

import com.woobeee.back.entity.UserInfo;
import com.woobeee.back.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;

    @Override
    public void signIn(String id, String loginId) {
        userInfoRepository.save(new UserInfo(id, UUID.fromString(loginId)));
    }
}
