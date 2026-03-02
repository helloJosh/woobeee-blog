package com.woobeee.back.repository;

import com.woobeee.back.entity.InbodyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InbodyRecordRepository extends JpaRepository<InbodyRecord, UUID> {
}
