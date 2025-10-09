package com.woobeee.sample.singlecompositekey.repository;

import com.woobeee.sample.singlecompositekey.entity.TestDataSingleChildren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestDataSingleChildrenRepository extends JpaRepository<TestDataSingleChildren, UUID> {
    List<TestDataSingleChildren> findTop10000ByOrderByStartedAtDesc();
}
