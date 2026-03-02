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
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecommendationBatch {

    @Id
    private UUID id;

    private String type;
    private String status;
    private Integer refreshCount;
    private Integer defaultReceiveLimit;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @Column(name = "proflies_id")
    private UUID profliesId;
}
