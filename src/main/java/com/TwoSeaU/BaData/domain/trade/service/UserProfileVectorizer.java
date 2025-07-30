package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileVectorizer {
    private static final double LIKES_POST_WEIGHT = 0.7; // 찜한 게시글의 가중치
    private static final double PURCHASE_POST_WEIGHT = 1.3; // 구매한 게시글의 가중치

    private final PostLikesRepository postLikesRepository;
    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final VectorUtils vectorUtils;

    //유저 행동 기반 벡터값 생성
    public double[] vectorizeUserProfile(User user) {
        UserProfile userProfile = createUserProfile(user);

        // 1. 카테고리 선호도 벡터
        double[] categoryPreferences = createPreferenceVector(
                userProfile.getCategoryPreferences(),
                PostVectorizer.categoryToIndex
        );

        // 2. 수치형 선호도 (가격, 마감 임박일수 등)
        double[] numericalPreferences = {
                vectorUtils.normalizePrice((int) userProfile.getPreparePrice()),
                vectorUtils.normalizeDaysToExpiry((int) userProfile.acceptableDaysToExpiry)
        };

        return vectorUtils.concatenate(categoryPreferences, numericalPreferences);
    }

    private List<Gifticon> getPostsByIds(List<Long> postIds) {
        List<Gifticon> posts = new ArrayList<>();

        for (Long postId : postIds) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));
            if (post instanceof Gifticon) {
                posts.add((Gifticon) post);
            }
        }
        return posts;
    }


    public UserProfile createUserProfile (User user) {
        Map<String, Double> userProfileCategoryPreferences = new HashMap<>();

        double tempPreparePrice = 0;
        double tempMaxAcceptableDaysToExpiry = 0;

        //구매
        List<Long> purchasedPostIds = paymentRepository.findBoughtPostIdByUserId(user.getId());
        List<Gifticon> purchasePosts = getPostsByIds(purchasedPostIds);

        for (Gifticon post : purchasePosts) {
            tempPreparePrice += post.getPrice().doubleValue() * PURCHASE_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += (post.getDeadLine().getDayOfYear() - LocalDate.now().getDayOfYear()) * PURCHASE_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0) + PURCHASE_POST_WEIGHT);
        }

        //찜
        List<Long> likedPostIds = postLikesRepository.findDistinctPostIdsByUserId(user.getId());
        List<Gifticon> likePosts = getPostsByIds(likedPostIds);

        for (Gifticon post : likePosts) {
            tempPreparePrice += post.getPrice().doubleValue() * LIKES_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += (post.getDeadLine().getDayOfYear() - LocalDate.now().getDayOfYear()) * LIKES_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0) + LIKES_POST_WEIGHT);
        }

        double total = userProfileCategoryPreferences.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (total > 0) {
            userProfileCategoryPreferences.replaceAll((category, value) -> value / total);
        }

        int totalPostCount = likePosts.size() + purchasePosts.size();

        return new UserProfile(
                userProfileCategoryPreferences,
                tempPreparePrice / totalPostCount,
                tempMaxAcceptableDaysToExpiry / totalPostCount
        );
    }

    // 카테고리 횟수 카운트
    private double[] createPreferenceVector(Map<String, Double> preferences, Map<String, Integer> mapping) {
        double[] vector = new double[mapping.size()];

        for (Map.Entry<String, Double> entry : preferences.entrySet()) {
            Integer index = mapping.get(entry.getKey());
            if (index != null && index < vector.length) {
                vector[index] = entry.getValue();
            }
        }

        return vector;
    }

    @Data
    @AllArgsConstructor
    public static class UserProfile {
        private Map<String, Double> categoryPreferences;
        private double preparePrice;
        private double acceptableDaysToExpiry;
    }
}