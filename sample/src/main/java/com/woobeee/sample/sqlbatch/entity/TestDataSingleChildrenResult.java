package com.woobeee.sample.sqlbatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(
        indexes = {
                @Index(name = "ix_single_children_ㅅㄷㄴㅅ_started_at", columnList = "started_at")
        }
)
public class TestDataSingleChildrenResult {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private UUID testDataSingleResultId;

}
