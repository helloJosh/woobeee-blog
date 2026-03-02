package com.woobeee.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Profile {

    @Id
    private UUID id;

    private String nickname;

    @Column(name = "instargram_id")
    private String instargramId;

    private String preferredRegion;

    @Column(columnDefinition = "text")
    private String introText;

    @Column(columnDefinition = "text")
    private String idealTypeText;

    private Integer yearsOfTraining;
}
