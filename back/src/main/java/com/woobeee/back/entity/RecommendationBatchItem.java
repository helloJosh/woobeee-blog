package com.woobeee.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecommendationBatchItem {

    @EmbeddedId
    private RecommendationBatchItemId id;

    private Integer position;

    @Column(name = "Field")
    private String field;

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class RecommendationBatchItemId implements Serializable {
        private UUID batchId;
        private UUID targetProfilesId;
    }
}
