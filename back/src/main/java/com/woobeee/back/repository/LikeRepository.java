package com.woobeee.back.repository;

import com.woobeee.back.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Like.LikeId> {
    Long countById_PostId(Long idPostId);
}
