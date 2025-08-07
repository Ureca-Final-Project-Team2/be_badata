package com.TwoSeaU.BaData.domain.trade.service.recommend.pgVector;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.JdbcRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.service.TrendingPostService;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.RecommendServiceFloat.RecommendationResult;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.UserProfileVectorizerFloat;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServicePg {
    final static String REDIS_KEY = "rec:user:";
    final static int RECOMMEND_LIMIT = 10;
    final static int TTL_LIMIT = 30;

    private final UserProfileVectorizerFloat userProfileVectorizerFloat;
    private final VectorUtilsPg vectorUtilsPg;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PostLikesRepository postLikesRepository;
    private final GifticonRepository gifticonRepository;
    private final TrendingPostService trendingPostService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcRepository jdbcRepository;

    public PostsResponse recommendPostsBypgVector(String username, final boolean isStart) {

        if (username == null) {
            return PostsResponse.of(trendingPostService.getTrendingPosts(null));
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        if (postLikesRepository.countByUserId(user.getId()) == 0
                && paymentRepository.countByUserIdAndPaymentStatus(user.getId(), PaymentStatus.PAID) == 0) {
            return PostsResponse.of(trendingPostService.getTrendingPosts(username));
        }

        return recommendContentBasedFilteringByPgvector(user, isStart);
    }

    private PostsResponse recommendContentBasedFilteringByPgvector (User user, final boolean isStart) {
        if(isStart){
            clearRecommendationCache(user.getId());
        }

        Long startTime = System.currentTimeMillis();

        Set<Long> recommendedPostsId = getRecommendedPostsCache(user.getId());

        try{
            float[] userVector = userProfileVectorizerFloat.vectorizeUserProfile(user, recommendedPostsId);

            List<RecommendationResult> candidatePosts = jdbcRepository.findSimilarPosts(userVector, recommendedPostsId.size());

            List<RecommendationResult> results = candidatePosts.stream()
                    .filter(postResponse -> !recommendedPostsId.contains(postResponse.getPost().getId()))
                    .sorted(Comparator.comparing(RecommendationResult::getFinalScore).reversed())
                    .limit(RECOMMEND_LIMIT)
                    .toList();
            //pg

            //종료 시간
            Long endTime = System.currentTimeMillis();
            System.out.println("추천 게시글 계산 시간: " + (endTime - startTime) + "ms");

            //출력
            for(RecommendationResult r : results) {
                log.info("추천 게시글: {}, 유사도: {}, 최종 점수: {}", r.getPost().getId(), r.getSimilarity(), r.getFinalScore());
            }

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
        catch (Exception e) {
            log.error("유저 벡터화 실패: {}", e);
            throw new GeneralException(TradeException.RECOMMENDATION_FAILED);
        }
    }

    public void addRecommendedPostCache(Long userId, List<RecommendationResult> postList) {
        String key = getKey(userId);

        for( RecommendationResult result : postList) {
            Long postId = result.getPost().getId();

            if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(key, postId.toString()))) {
                redisTemplate.opsForSet().add(key, postId.toString());
                redisTemplate.expire(key, Duration.ofMinutes(TTL_LIMIT));
            }
        }
    }

    public Set<Long> getRecommendedPostsCache(Long userId) {
        String key = getKey(userId);

        try {
            Set<Object> postSet = redisTemplate.opsForSet().members(key);

            if (postSet == null) {
                return Set.of();
            }

            return postSet.stream()
                    .map(Object::toString)
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }
        catch (Exception e) {
            log.warn("{} userId에 대해 Redis에서 가져오는 것을 실패했습니다.", userId, e);
            return Set.of();
        }

    }

    private String getKey(Long userId) {
        return REDIS_KEY + userId;
    }

    public void clearRecommendationCache(Long userId) {
        try{
            redisTemplate.delete(getKey(userId));
        }
        catch (Exception e) {
            log.warn("{} userId에 대해 Redis를 clear하는 것을 실패했습니다.", userId, e);

        }
    }
}
