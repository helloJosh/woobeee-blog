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
public class InbodyRecord {

    @Id
    private UUID id;

    private Integer weight;
    private Integer skeletalMuscle;
    private Integer bodyFatPercent;
    private Integer visceralFat;
    private LocalDateTime measuredAt;
    private Boolean isVerified;
    private String imageUrl;
    private UUID profilesId;
}
