package com.woobeee.blog.post.entity;

import com.woobeee.blog.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String context;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne(optional = false)
    private Member member;

    @ManyToOne(optional = false)
    private Post post;

    @ManyToOne
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public void addChildren(Comment child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        child.setParent(this);
        this.children.add(child);
    }

    public Comment(String context, Member member, Post post) {
        this.context = context;
        this.member = member;
        this.post = post;
    }
}
