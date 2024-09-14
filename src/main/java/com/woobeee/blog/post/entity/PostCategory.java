package com.woobeee.blog.post.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime updatedAt;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Post post;

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
    }

    public PostCategory(Post post, Category category) {
        this.post = post;
        this.category = category;
    }
}
