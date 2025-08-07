package com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector;

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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileVectorizerFloat {
    private static final float CATEGORY_WEIGHT = 1.3F;
    private static final float PARTNER_WEIGHT = 0.3F;
    private static final float PRICE_WEIGHT = 0.9F;
    private static final float DAYS_TO_EXPIRY_WEIGHT = 0.5F;

    private static final float LIKES_POST_WEIGHT = 0.6F; // 찜한 게시글의 가중치
    private static final float PURCHASE_POST_WEIGHT = 1.5F; // 구매한 게시글의 가중치
    private static final float RECOMMENDATION_LIKES_POST_WEIGHT = 0.9F; // 추천 게시글의 가중치

    private final PostLikesRepository postLikesRepository;
    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final VectorUtilsFloat vectorUtilsFloat;

    //유저 행동 기반 벡터값 생성
    public float[] vectorizeUserProfile(final User user, final Set<Long> recommendedPostIds) {
        final UserProfile userProfile = createUserProfile(user, recommendedPostIds);

        // 1. 카테고리 선호도 벡터
        final float[] categoryPreferences = createPreferenceVector(
                userProfile.getCategoryPreferences(),
                PostVectorizerFloat.categoryToIndex
        );

        // 2. 제휴사 선호도 벡터
        final float[] partnerPreferences = createPreferenceVector(
                userProfile.getPartnerPreferences(),
                PostVectorizerFloat.partnerToIndex
        );

        for(int i = 0; i < categoryPreferences.length; i++) {
            categoryPreferences[i] *= CATEGORY_WEIGHT;
        }

        for(int i = 0; i < partnerPreferences.length; i++) {
            partnerPreferences[i] *= PARTNER_WEIGHT;
        }

        // 3. 수치형 선호도 (가격, 마감 임박일수 등)
        final float[] numericalPreferences = {
                userProfile.getPreparePrice() * PRICE_WEIGHT,
                vectorUtilsFloat.normalizeDaysToExpiry((int) userProfile.acceptableDaysToExpiry) * DAYS_TO_EXPIRY_WEIGHT
        };

        return vectorUtilsFloat.concatenate(categoryPreferences, partnerPreferences, numericalPreferences);
    }

    public List<Gifticon> getPostsByIds(List<Long> postIds) {
        return postRepository.findAllById(postIds).stream()
                .filter(post -> post instanceof Gifticon)
                .map(post -> (Gifticon) post)
                .collect(Collectors.toList());
    }


    public UserProfile createUserProfile (final User user, final Set<Long> recommendedPostIds) {
        final Map<String, Float> userProfileCategoryPreferences = new HashMap<>();
        final Map<String, Float> userProfilePartnerPreferences = new HashMap<>();

        float tempPreparePrice = 0;
        float tempMaxAcceptableDaysToExpiry = 0;

        //구매
        List<Long> purchasedPostIds = paymentRepository.findBoughtPostIdByUserId(user.getId());
        List<Gifticon> purchasePosts = getPostsByIds(purchasedPostIds);

        for (Gifticon post : purchasePosts) {
            tempPreparePrice += post.getPrice().floatValue() * PURCHASE_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadLine()) * PURCHASE_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0F) + PURCHASE_POST_WEIGHT);

            String partner = post.getPartner();
            userProfilePartnerPreferences.put(partner,
                    userProfilePartnerPreferences.getOrDefault(partner, 0.0F) + PURCHASE_POST_WEIGHT);
        }

        //찜
        List<Long> likedPostIds = postLikesRepository.findDistinctPostIdsByUserId(user.getId());
        List<Gifticon> likePosts = getPostsByIds(likedPostIds);

        for (Gifticon post : likePosts) {
            tempPreparePrice += post.getPrice().floatValue() * LIKES_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadLine()) * LIKES_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0F) + LIKES_POST_WEIGHT);

            String partner = post.getPartner();
            userProfilePartnerPreferences.put(partner,
                    userProfilePartnerPreferences.getOrDefault(partner, 0.0F) + LIKES_POST_WEIGHT);
        }

        //추천 시 좋아요
        List<Gifticon> recommendLikePosts = getPostsByIds(new ArrayList<>(recommendedPostIds));

        for (Gifticon post : recommendLikePosts) {
            tempPreparePrice += post.getPrice().floatValue() * RECOMMENDATION_LIKES_POST_WEIGHT;
            tempMaxAcceptableDaysToExpiry += ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadLine()) * RECOMMENDATION_LIKES_POST_WEIGHT;

            GifticonCategory category = post.getCategory();
            userProfileCategoryPreferences.put(category.getCategoryName(),
                    userProfileCategoryPreferences.getOrDefault(category.getCategoryName(), 0.0F) + RECOMMENDATION_LIKES_POST_WEIGHT);

            String partner = post.getPartner();
            userProfilePartnerPreferences.put(partner,
                    userProfilePartnerPreferences.getOrDefault(partner, 0.0F) + RECOMMENDATION_LIKES_POST_WEIGHT);
        }

        //정규화
        float totalCategory = userProfileCategoryPreferences.values().stream()
                .reduce(0.0F, Float::sum);

        float totalPartner = userProfilePartnerPreferences.values().stream()
                .reduce(0.0F, Float::sum);

        if (totalCategory > 0) {
            userProfileCategoryPreferences.replaceAll((category, value) -> value / totalCategory);
        }

        if (totalPartner > 0) {
            userProfilePartnerPreferences.replaceAll((partner, value) -> value / totalPartner);
        }

        int totalPostCount = likePosts.size() + purchasePosts.size() + recommendLikePosts.size();

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
    public float[] createPreferenceVector(Map<String, Float> preferences, Map<String, Integer> mapping) {
        float[] vector = new float[mapping.size()];

        for (Map.Entry<String, Float> entry : preferences.entrySet()) {
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
        private Map<String, Float> categoryPreferences;
        private Map<String, Float> partnerPreferences;
        private float preparePrice;
        private float acceptableDaysToExpiry;
    }
}