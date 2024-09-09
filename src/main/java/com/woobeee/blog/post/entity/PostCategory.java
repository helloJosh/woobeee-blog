package com.woobeee.blog.post.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Post post;

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
    }
}
