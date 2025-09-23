package com.woobeee.back.support;

import com.woobeee.back.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisSupport {
    private static final String KEY_POST_VIEWS = "post:views:";
    private static final String ZSET_POST_VIEWS = "post:views:ranking";

    private final StringRedisTemplate redisTemplate;

    public long incrementPostViewAndRanking(long postId) {
        Long after = redisTemplate.opsForValue().increment(KEY_POST_VIEWS + postId);

        redisTemplate.opsForZSet().incrementScore(ZSET_POST_VIEWS, String.valueOf(postId), 1.0);

        return after == null ? 0L : after;
    }

    public long getRedisOnlyViews(long postId) {
        String v = redisTemplate.opsForValue().get(KEY_POST_VIEWS + postId);
        return (v == null) ? 0L : Long.parseLong(v);
    }

    public List<Long> getTopViewedPostIds(int limit) {
        Set<String> ids = redisTemplate.opsForZSet()
                .reverseRange(ZSET_POST_VIEWS, 0, Math.max(0, limit - 1));
        if (ids == null || ids.isEmpty()) return List.of();
        return ids.stream().map(Long::valueOf).toList();
    }

    public long getTotalViews(Post post) {
        long redisIncr = getRedisOnlyViews(post.getId());
        long dbBase = post.getViews() == null ? 0L : post.getViews();
        return dbBase + redisIncr;
    }
}
