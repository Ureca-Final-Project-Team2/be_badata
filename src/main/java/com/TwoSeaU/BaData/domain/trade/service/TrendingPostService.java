package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrendingPostService {
    private final PostLikesRepository postLikesRepository;
    private final PostRepository postsRepository;
    private final UserRepository userRepository;
    private final RedisTemplate <String, Object> redisTemplate;

    private static final String REDIS_KEY = "popular_posts";
    private static final Duration TTL = Duration.ofHours(3);
    private static final int POSTS_LIMIT = 5;

    public List<PostResponse> getTrendingPosts(String username) {
        final User loginUser = username == null ? null : userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Set<Object> members = redisTemplate.opsForZSet()
                .reverseRange(REDIS_KEY, 0, -1);

        final List<Long> postIdList = members.stream()
                .map(member -> Long.parseLong(member.toString().replace("post:", "")))
                .toList();

        final List<Post> resultPosts = new LinkedList<>();

        for (Long postId : postIdList) {
            Post post = postsRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

            if (!post.getIsDeleted() && !post.getIsSold()) {
                resultPosts.add(post);
            }

            if (resultPosts.size() == POSTS_LIMIT) {
                break;
            }
        }

        if (resultPosts.size() < POSTS_LIMIT) {
            resultPosts.addAll(postsRepository.getRecentPostsBySize(POSTS_LIMIT - resultPosts.size()));
        }

        return resultPosts
                .stream()
                .map(post ->
                        PostResponse.from(
                                post,
                                postLikesRepository.countByPostId(post.getId()),
                                username != null && postLikesRepository.existsByUserIdAndPostId(loginUser.getId(), post.getId())
                        )
                )
                .toList();
    }

    private void updateScore(Long postId, int increment) {
        String key = REDIS_KEY;
        String member = getRedisMember(postId);

        redisTemplate.opsForZSet().incrementScore(key, member, increment);

        if (redisTemplate.getExpire(key) == -1) {
            redisTemplate.expire(key, TTL);
        }
    }

    private String getRedisMember(Long postId) {
        return "post:" + postId;
    }

    public void recordView(Long postId) {
        updateScore(postId, 1);
    }

    public void recordLike(Long postId) {
        updateScore(postId, 3);
    }
}
