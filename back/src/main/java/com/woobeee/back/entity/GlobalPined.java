package com.woobeee.back.entity;

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
public class GlobalPined {

    @Id
    private UUID id;

    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Integer priorityScore;
    private String status;
    private UUID profilesId;
}
