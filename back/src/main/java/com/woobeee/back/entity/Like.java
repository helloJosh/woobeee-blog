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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    @MapsId(value = "userId")
//    private UserInfo userInfo;
//    private UUID userId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    @MapsId(value = "postId")
//    private Post post;
//    private Long postId;

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
