package com.woobeee.back.repository.test;

import com.woobeee.back.entity.test.TestDataChildren;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDataChildrenRepository extends JpaRepository<TestDataChildren, TestDataChildren.TestDataChildrenId> {
}
