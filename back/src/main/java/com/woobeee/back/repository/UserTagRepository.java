package com.woobeee.back.repository;

import com.woobeee.back.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTagRepository extends JpaRepository<UserTag, UserTag.UserTagId> {
}
