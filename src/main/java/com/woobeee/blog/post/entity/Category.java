package com.woobeee.blog.post.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PostCategory> postCategories = new ArrayList<>();

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void addChildren(Category child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        child.setParent(this);
        this.children.add(child);
    }

    public Category(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
