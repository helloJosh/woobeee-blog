package com.woobeee.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class TestDataSingle {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public TestDataSingle(LocalDateTime startedAt, LocalDateTime endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
