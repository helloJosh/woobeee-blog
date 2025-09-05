package com.woobeee.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserAuth {
    @EmbeddedId
    private UserAuthId id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    @MapsId(value = "userId")
//    private UserCredential userCredential;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "auth_id")
//    @MapsId(value = "authId")
//    private Auth auth;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class UserAuthId implements Serializable {
        private UUID userId;
        private Long authId;
    }

    public Long getAuthId(){
        return getId().authId;
    }
}
