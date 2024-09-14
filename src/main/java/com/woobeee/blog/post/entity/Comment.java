package com.woobeee.blog.post.entity;

import com.woobeee.blog.member.entity.Member;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String context;

    @Setter
    private Status status;

    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
    @Setter
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

    public Comment(String context, Comment parent, Member member, Post post) {
        this.parent = parent;
        this.context = context;
        this.member = member;
        this.post = post;
    }

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
        status = Status.ACTIVE;
    }
}
