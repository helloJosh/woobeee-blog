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
public class RunningRecord {

    @Id
    private UUID id;

    private Integer records;
    private LocalDateTime recordedAt;
    private Boolean isVerified;
    private UUID profilesId;
}
