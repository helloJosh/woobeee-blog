package com.woobeee.back.service;


public interface LikeService {
    void saveLike(Long postId, String userId);
    void deleteLike(Long postId, String userId);
}
