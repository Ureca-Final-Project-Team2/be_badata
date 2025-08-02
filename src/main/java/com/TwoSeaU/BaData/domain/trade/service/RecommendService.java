package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    final static String REDIS_KEY = "rec:user:";
    final static int RECOMMEND_LIMIT = 10;

    private final UserProfileVectorizer userProfileVectorizer;
    private final VectorUtils vectorUtils;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PostLikesRepository postLikesRepository;
    private final GifticonRepository gifticonRepository;
    private final TrendingPostService trendingPostService;
    private final RedisTemplate<String, Object> redisTemplate;

    //인기순, 추천순 분기
    public PostsResponse recommendPosts(String username, final boolean isStart) {
        if (username == null) {
            return PostsResponse.of(trendingPostService.getTrendingPosts(null));
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        if (postLikesRepository.countByUserId(user.getId()) == 0
                && paymentRepository.countByUserIdAndPaymentStatus(user.getId(), PaymentStatus.PAID) == 0) {
            return PostsResponse.of(trendingPostService.getTrendingPosts(username));
        }

        return recommendContentBasedFiltering(user, isStart);

    }

    private PostsResponse recommendContentBasedFiltering (User user, final boolean isStart) {
        double[] userVector = userProfileVectorizer.vectorizeUserProfile(user);

        if(isStart){
            clearRecommendationCache(user.getId());
        }

        List<Gifticon> candidatePosts = gifticonRepository.getAllSales(user.getUsername(), getRecommendedPostsCache(user.getId()));

        List<RecommendationResult> results = candidatePosts.parallelStream()
                .map(post -> {
                    double[] postVector = post.getVector();

                    //유사도 계산
                    double similarity = vectorUtils.calculateWeightedSimilarity(userVector, postVector);

                    //최종 점수 계산 (추후 최종 점수에 인기도 반영)
                    double finalScore = similarity;

                    return new RecommendationResult(post, similarity, finalScore);
                })
                .sorted(Comparator.comparing(RecommendationResult::getFinalScore).reversed())
                .limit(RECOMMEND_LIMIT)
                .toList();

        List<PostResponse> finalResult = results.stream()
                .map(result ->
                        PostResponse.from(
                                result.getPost(),
                                postLikesRepository.countByPostId(result.getPost().getId()),
                                postLikesRepository.existsByUserIdAndPostId(user.getId(), result.getPost().getId())
                        )
                )
                .collect(Collectors.toList());

        addRecommendedPostCache(user.getId(), results);

        return PostsResponse.of(finalResult);
    }

    public void addRecommendedPostCache(Long userId, List<RecommendationResult> postList) {
        String key = getKey(userId);

        for( RecommendationResult result : postList) {
            Long postId = result.getPost().getId();

            if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(key, postId.toString()))) {
                redisTemplate.opsForSet().add(key, postId.toString());
                redisTemplate.expire(key, Duration.ofMinutes(30));
            }
        }
    }

    public Set<Long> getRecommendedPostsCache(Long userId) {
        String key = getKey(userId);
        Set<Object> postSet = redisTemplate.opsForSet().members(key);

        if (postSet == null) {
            return Set.of();
        }

        return postSet.stream()
                .map(Object::toString)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    private String getKey(Long userId) {
        return REDIS_KEY + userId;
    }

    public void clearRecommendationCache(Long userId) {
        String key = REDIS_KEY + userId;
        redisTemplate.delete(key);
    }

    @Data
    @AllArgsConstructor
    public static class RecommendationResult {
        private Gifticon post;
        private double similarity;
        private double finalScore;
    }
}
