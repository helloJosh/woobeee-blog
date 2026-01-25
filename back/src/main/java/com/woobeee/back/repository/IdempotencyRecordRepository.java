package com.woobeee.back.repository;

import com.woobeee.back.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByClientIdAndDomainKey(String clientId, String domainKey);
}
