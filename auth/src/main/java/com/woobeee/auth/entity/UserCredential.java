package com.woobeee.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserCredential {
    /**
     * This UUID id must be same with UserInfo Tables Id
     * Later the database will be seperated
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String loginId;

    /**
     * This parameter could be
     * Google Oauth member unique member key
     * or custom members password
     */
    private String password;

    @CreationTimestamp
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt;


//    @OneToMany(mappedBy = "userCredential", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserAuth> userAuths = new ArrayList<>();
}
