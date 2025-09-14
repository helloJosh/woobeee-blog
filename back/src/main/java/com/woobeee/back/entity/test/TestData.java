package com.woobeee.back.entity.test;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TestData {
    @EmbeddedId
    private TestDataId testDataId;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestDataId implements Serializable {
        @Column(name = "id1")
        private UUID id1;

        @Column(name = "id2")
        private UUID id2;
    }
}
