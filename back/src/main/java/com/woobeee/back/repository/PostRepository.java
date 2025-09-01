package com.woobeee.back.repository;

import com.woobeee.back.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    interface CategoryCount {
        Long getCategoryId();
        long getCnt();
    }

    @Query(
            value =
            """
                SELECT p.category_id AS categoryId, COUNT(*) AS cnt
                FROM post p
                WHERE p.category_id IN (:categoryIds)
                GROUP BY p.category_id
            """,
            nativeQuery = true
    )
    List<CategoryCount> countGroupByCategoryId(@Param("categoryIds") Collection<Long> categoryIds);

    List<Post> findAllByCategoryIdIn(List<Long> ids);
    void deleteAllByCategoryIdIn(List<Long> ids);
}
