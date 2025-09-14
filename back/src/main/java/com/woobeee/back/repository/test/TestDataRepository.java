package com.woobeee.back.repository.test;

import com.woobeee.back.entity.test.TestData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDataRepository extends JpaRepository<TestData, TestData.TestDataId> {
}
