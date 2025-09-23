package com.woobeee.test.repository;

import com.woobeee.test.entity.TestData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDataRepository extends JpaRepository<TestData, TestData.TestDataId> {
}
