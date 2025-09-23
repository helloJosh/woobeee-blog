package com.woobeee.test.repository;

import com.woobeee.test.entity.TestDataSingle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestDataSingleRepository extends JpaRepository<TestDataSingle, UUID> {
}
