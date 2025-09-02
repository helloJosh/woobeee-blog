package com.woobeee.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titleKo;
    private String titleEn;

    @Column(columnDefinition = "text")
    private String textKo;
    @Column(columnDefinition = "text")
    private String textEn;

    private Long views;

    @CreationTimestamp
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Category category;
    private Long categoryId;
    private UUID userId;

    public Post(String titleKo, String titleEn, String textKo, String textEn, Long categoryId, UUID userId) {
        this.titleKo = titleKo;
        this.titleEn = titleEn;
        this.textKo = textKo;
        this.textEn = textEn;
        this.categoryId = categoryId;
        this.userId = userId;
    }
}
