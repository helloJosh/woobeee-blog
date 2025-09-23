package com.woobeee.back.test.repository;

import com.woobeee.back.test.entity.TestDataChildren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDataChildrenRepository extends JpaRepository<TestDataChildren, TestDataChildren.TestDataChildrenId> {
    List<TestDataChildren> findTop10000ByOrderByStartedAtDesc();
}
