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
@Table(
    indexes = {
            @Index(name = "ix_children_started_at", columnList = "started_at")
    }
)
public class TestDataChildren {
    @EmbeddedId
    private TestDataChildrenId testDataId;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @ManyToOne
    @MapsId("testDataId") // ★ 자식 ID 안의 'testDataId' 필드를 이 연관관계 키로 사용
    @JoinColumns({
            @JoinColumn(name = "parent_id1", referencedColumnName = "id1", nullable = false),
            @JoinColumn(name = "parent_id2", referencedColumnName = "id2", nullable = false)
    })
    private TestData testData;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestDataChildrenId implements Serializable {
        @Column(name = "id1")
        private UUID id1;

        @Column(name = "id2")
        private UUID id2;

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name="id1", column=@Column(name="parent_id1", nullable=false)),
                @AttributeOverride(name="id2", column=@Column(name="parent_id2", nullable=false))
        })
        private TestData.TestDataId testDataId;
    }
}
