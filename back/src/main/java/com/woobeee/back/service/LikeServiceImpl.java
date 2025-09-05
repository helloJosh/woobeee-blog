package com.woobeee.back.service;

import com.woobeee.back.entity.Like;
import com.woobeee.back.entity.UserInfo;
import com.woobeee.back.exception.CustomAuthenticationException;
import com.woobeee.back.exception.CustomNotFoundException;
import com.woobeee.back.exception.ErrorCode;
import com.woobeee.back.repository.LikeRepository;
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
public class LikeServiceImpl implements LikeService{
    private final LikeRepository likeRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public void saveLike(Long postId, String loginId) {

        if (loginId == null) {
            throw new CustomAuthenticationException(ErrorCode.like_needAuthentication);
        }

        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Like like = new Like(userInfo.getId(), postId);
        likeRepository.save(like);
    }

    @Override
    public void deleteLike(Long postId, String loginId) {
        if (loginId == null) {
            throw new CustomAuthenticationException(ErrorCode.like_needAuthentication);
        }

        UserInfo userInfo = userInfoRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.login_userNotFound));

        Like like = likeRepository
                .findById(new Like.LikeId(userInfo.getId(), postId))
                .orElseThrow();

        likeRepository.delete(like);
    }
}
