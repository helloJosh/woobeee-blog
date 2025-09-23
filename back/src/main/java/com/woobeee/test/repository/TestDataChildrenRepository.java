package com.woobeee.test.repository;

import com.woobeee.test.entity.TestDataChildren;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDataChildrenRepository extends JpaRepository<TestDataChildren, TestDataChildren.TestDataChildrenId> {
}
