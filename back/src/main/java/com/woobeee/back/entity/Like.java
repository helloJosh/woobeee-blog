package com.woobeee.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Like {

    @EmbeddedId
    private LikeId id;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class LikeId implements Serializable {
        private UUID userId;
        private Long postId;
    }
}
