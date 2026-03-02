package com.woobeee.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Outbox {

    @Id
    private String uuid;

    private Short type;
    private Short status;
    private String topic;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private Integer attempts;
    private String lastError;
    private LocalDateTime createdAt;
    private LocalDateTime lockedAt;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime sentAt;
}
