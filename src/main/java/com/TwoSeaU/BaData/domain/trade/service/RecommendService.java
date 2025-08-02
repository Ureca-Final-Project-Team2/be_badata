package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveRecommendLikesResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    final static int RECOMMEND_LIMIT = 10;

    final UserProfileVectorizer userProfileVectorizer;
    final VectorUtils vectorUtils;
    final UserRepository userRepository;
    final PaymentRepository paymentRepository;
    final PostLikesRepository postLikesRepository;
    final GifticonRepository gifticonRepository;
    final MockService mockService;

    public SaveRecommendLikesResponse likeRecommendation(String username, Long postId) {
        if (username == null) {
            return SaveRecommendLikesResponse.of(false);
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Gifticon post = gifticonRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        PostLikes postLikes = PostLikes.of(post, user);

        if (postLikesRepository.existsByUserIdAndPostId(user.getId(), postId)){
            return SaveRecommendLikesResponse.of(false);
        }

        postLikesRepository.save(postLikes);
        return SaveRecommendLikesResponse.of(true);
    }

    //인기순, 추천순 분기
    public PostsResponse recommendPosts(String username) {
        if (username == null) {
            return mockService.getHotPosts();
        }

        else {
            final User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

            if (postLikesRepository.countByUserId(user.getId()) == 0
                    && paymentRepository.countByUserIdAndPaymentStatus(user.getId(), PaymentStatus.PAID) == 0) {
                return mockService.getHotPosts();
            }

            return recommendContentBasedFiltering(user);
        }
    }

    //추천순
    private PostsResponse recommendContentBasedFiltering (User user) {
        double[] userVector = userProfileVectorizer.vectorizeUserProfile(user);

        List<Gifticon> candidatePosts = gifticonRepository.findByIsSoldAndIsDeletedAndDeadLineGreaterThanEqual(false, false, LocalDate.now());

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

        return PostsResponse.of(finalResult);
    }

    //인기순
    private PostsResponse recommendPopularPosts() {
        // 인기순으로 게시글을 가져오는 메소드.
        // 추후 인기순 게시글 개발 예정이므로 지금은 null을 return합니다
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class RecommendationResult {
        private Gifticon post;
        private double similarity;
        private double finalScore;
    }
}
