package com.woobeee.blog.member.entity;

import com.woobeee.blog.post.entity.Post;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Post post;
}
