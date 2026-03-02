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
public class MatchRequest {

    @Id
    private String id;

    private String fromProfilesId;
    private String toProfileId;
    private String status;
    private LocalDateTime createdAt;

    @Column(name = "responed_at")
    private LocalDateTime responedAt;

    @Column(columnDefinition = "text")
    private String requestText;

    private UUID batchId;
}
