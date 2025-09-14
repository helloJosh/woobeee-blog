package com.woobeee.back.repository.test;

import com.woobeee.back.entity.test.TestDataSingle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestDataSingleRepository extends JpaRepository<TestDataSingle, UUID> {
}
