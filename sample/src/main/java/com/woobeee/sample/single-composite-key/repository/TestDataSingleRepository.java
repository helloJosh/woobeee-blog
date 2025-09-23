package com.woobeee.back.test.repository;

import com.woobeee.back.test.entity.TestDataSingle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestDataSingleRepository extends JpaRepository<TestDataSingle, UUID> {
    List<TestDataSingle> findTop1ByOrderByStartedAtDesc();
}
