package com.woobeee.blog.post.entity;

import com.woobeee.blog.member.entity.Like;
import com.woobeee.blog.post.entity.enums.Status;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String context;
    private Status status;
    private Long count;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PostConstruct
    private void postConstruct() {
        createdAt = LocalDateTime.now();
        status = Status.ACTIVE;
        count = 0L;
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PostCategory> postCategories = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Post(String title, String context, Long count) {
        this.title = title;
        this.context = context;
        this.count = count;
    }

    /**
     * 포스트태그 저장 메소드.
     *
     * @param post 게시글
     * @param tag 태그
     */
    public void addPostTag(Post post, Tag tag){
        PostTag postTag = new PostTag(post, tag);
        this.postTags.add(postTag);
    }

    /**
     * 포스트태그 저장 메소드.
     *
     * @param post 게시글
     * @param category 태그
     */
    public void addPostCategory(Post post, Category category){
        PostCategory postCategory = new PostCategory(post, category);
        this.postCategories.add(postCategory);
    }

}
