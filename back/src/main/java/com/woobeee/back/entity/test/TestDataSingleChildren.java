package com.woobeee.back.entity.test;

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
                @Index(name = "ix_single_children_started_at", columnList = "started_at")
        }
)
public class TestDataSingleChildren {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @ManyToOne
    private TestDataSingle testDataSingle;

    public TestDataSingleChildren(LocalDateTime startedAt, LocalDateTime endedAt, TestDataSingle testDataSingle) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.testDataSingle = testDataSingle;
    }
}
