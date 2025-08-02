package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        final UserProfile userProfile = createUserProfile(user);

        // 1. 카테고리 선호도 벡터
        final double[] categoryPreferences = createPreferenceVector(
                userProfile.getCategoryPreferences(),
                PostVectorizer.categoryToIndex
        );

        // 2. 제휴사 선호도 벡터
        final double[] partnerPreferences = createPreferenceVector(
                userProfile.getCategoryPreferences(),
                PostVectorizer.partnerToIndex
        );

        // 3. 수치형 선호도 (가격, 마감 임박일수 등)
        final double[] numericalPreferences = {
                vectorUtils.normalizePrice((int) userProfile.getPreparePrice()),
                vectorUtils.normalizeDaysToExpiry((int) userProfile.acceptableDaysToExpiry)
        };

        return vectorUtils.concatenate(categoryPreferences, partnerPreferences, numericalPreferences);
    }

    private List<Gifticon> getPostsByIds(List<Long> postIds) {
        return postRepository.findAllById(postIds).stream()
                .filter(post -> post instanceof Gifticon)
                .map(post -> (Gifticon) post)
                .collect(Collectors.toList());
    }


    public UserProfile createUserProfile (User user) {
        final Map<String, Double> userProfileCategoryPreferences = new HashMap<>();
        final Map<String, Double> userProfilePartnerPreferences = new HashMap<>();

        double tempPreparePrice = 0;
        double tempMaxAcceptableDaysToExpiry = 0;

        //구매
        List<Long> purchasedPostIds = paymentRepository.findBoughtPostIdByUserId(user.getId());
        List<Gifticon> purchasePosts = getPostsByIds(purchasedPostIds);

        for (Gifticon post : purchasePosts) {
            tempPreparePrice += post.getPrice().doubleValue() * PURCHASE_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadLine()) * PURCHASE_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0) + PURCHASE_POST_WEIGHT);

            String partner = post.getPartner();
            userProfilePartnerPreferences.put(partner,
                    userProfilePartnerPreferences.getOrDefault(partner, 0.0) + PURCHASE_POST_WEIGHT);
        }

        //찜
        List<Long> likedPostIds = postLikesRepository.findDistinctPostIdsByUserId(user.getId());
        List<Gifticon> likePosts = getPostsByIds(likedPostIds);

        for (Gifticon post : likePosts) {
            tempPreparePrice += post.getPrice().doubleValue() * LIKES_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadLine()) * LIKES_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0) + LIKES_POST_WEIGHT);

            String partner = post.getPartner();
            userProfilePartnerPreferences.put(partner,
                    userProfilePartnerPreferences.getOrDefault(partner, 0.0) + PURCHASE_POST_WEIGHT);
        }

        //정규화
        double totalCategory = userProfileCategoryPreferences.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalPartner = userProfilePartnerPreferences.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (totalCategory > 0) {
            userProfileCategoryPreferences.replaceAll((category, value) -> value / totalCategory);
            userProfilePartnerPreferences.replaceAll((partner, value) -> value / totalPartner);
        }

        int totalPostCount = likePosts.size() + purchasePosts.size();

        if(totalPostCount == 0) {
            throw new GeneralException(TradeException.RECOMMENDATION_FAILED);
        }

        return new UserProfile(
                userProfileCategoryPreferences,
                userProfilePartnerPreferences,
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
        private Map<String, Double> partnerPreferences;
        private double preparePrice;
        private double acceptableDaysToExpiry;
    }
}