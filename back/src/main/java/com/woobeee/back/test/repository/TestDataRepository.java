package com.woobeee.back.test.repository;

import com.woobeee.back.test.entity.TestData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDataRepository extends JpaRepository<TestData, TestData.TestDataId> {
    List<TestData> findTop1ByOrderByStartedAtDesc();
}
