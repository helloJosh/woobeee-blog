package com.woobeee.sample.singlecompositekey.repository;

import com.woobeee.sample.singlecompositekey.entity.TestData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDataRepository extends JpaRepository<TestData, TestData.TestDataId> {
    List<TestData> findTop1ByOrderByStartedAtDesc();
}
