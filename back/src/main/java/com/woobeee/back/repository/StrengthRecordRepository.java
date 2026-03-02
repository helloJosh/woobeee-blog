package com.woobeee.back.repository;

import com.woobeee.back.entity.StrengthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StrengthRecordRepository extends JpaRepository<StrengthRecord, UUID> {
}
