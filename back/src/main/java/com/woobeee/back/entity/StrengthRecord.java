package com.woobeee.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "strength_records")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StrengthRecord {

    @Id
    private UUID id;

    private Integer squat;
    private Integer bench;
    private Integer deadlift;
    private Integer recordedAt;
    private Boolean isVerified;
    private UUID profilesId;
}
