package com.woobeee.back.repository.test;

import com.woobeee.back.entity.test.TestDataSingle;
import com.woobeee.back.entity.test.TestDataSingleChildren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestDataSingleChildrenRepository extends JpaRepository<TestDataSingleChildren, UUID> {
}
