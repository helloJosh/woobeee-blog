package com.woobeee.back.repository;

import com.woobeee.back.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, UUID> {
}
