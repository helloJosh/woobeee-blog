package com.woobeee.blog.post.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Tag tag;

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
    }

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}
