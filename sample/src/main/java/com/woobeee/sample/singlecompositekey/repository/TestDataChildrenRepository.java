package com.woobeee.sample.singlecompositekey.repository;

import com.woobeee.sample.singlecompositekey.entity.TestDataChildren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDataChildrenRepository extends JpaRepository<TestDataChildren, TestDataChildren.TestDataChildrenId> {
    List<TestDataChildren> findTop10000ByOrderByStartedAtDesc();
}
