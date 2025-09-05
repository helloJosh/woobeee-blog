package com.woobeee.back.service;

import com.woobeee.back.entity.Like;
import com.woobeee.back.repository.LikeRepository;
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

    @Override
    public void saveLike(Long postId, String userId) {
        Like like = new Like(UUID.fromString(userId), postId);
        likeRepository.save(like);
    }

    @Override
    public void deleteLike(Long postId, String userId) {
        Like like = likeRepository
                .findById(new Like.LikeId(UUID.fromString(userId), postId))
                .orElseThrow();

        likeRepository.delete(like);
    }
}
