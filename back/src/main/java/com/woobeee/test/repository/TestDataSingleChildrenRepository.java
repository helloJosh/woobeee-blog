package com.woobeee.test.repository;

import com.woobeee.test.entity.TestDataSingleChildren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestDataSingleChildrenRepository extends JpaRepository<TestDataSingleChildren, UUID> {
}
