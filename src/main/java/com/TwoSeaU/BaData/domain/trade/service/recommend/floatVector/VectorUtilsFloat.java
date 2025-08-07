package com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector;

import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector.PostVectorizerDouble;
import com.TwoSeaU.BaData.global.response.GeneralException;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class VectorUtilsFloat {
    private static final int CATEGORY_COUNT = PostVectorizerDouble.categoryToIndex.size();
    private static final int PARTNER_COUNT = PostVectorizerDouble.partnerToIndex.size();
    private static final int PRICE_INDEX = CATEGORY_COUNT + PARTNER_COUNT;
    private static final int DAYS_TO_EXPIRY_INDEX = PRICE_INDEX + 1;

    // 가중치
    private static final float CATEGORY_WEIGHT = 1.3F;
    private static final float PARTNER_WEIGHT = 0.3F;
    private static final float PRICE_WEIGHT = 0.9F;
    private static final float DAYS_TO_EXPIRY_WEIGHT = 0.5F;

    private static final float MAX_PRICE = 30000.0F;
    private static final float MAX_DAYS_TO_EXPIRY = 365.0F;

    public float calculateFigureSimilarity (final float userValue, final float postValue, final float maximum) {
        final float difference = Math.abs(userValue - postValue);

        float result = Math.max(0.0F, 1.0F - (difference / maximum));
        return result;
    }

    public float calculateCategorySimilarity (float[] user, float[] post) {
        float dotProduct = 0.0F;
        float normA = 0.0F;
        float normB = 0.0F;

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            dotProduct += user[i] * post[i];
            normA += Math.pow(user[i], 2);
            normB += Math.pow(post[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0F;
        }

        return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    public float calculatePartnerSimilarity (float[] user, float[] post) {
        float dotProduct = 0.0F;
        float normA = 0.0F;
        float normB = 0.0F;

        for (int i = CATEGORY_COUNT; i < CATEGORY_COUNT + PARTNER_COUNT; i++) {
            dotProduct += user[i] * post[i];
            normA += Math.pow(user[i], 2);
            normB += Math.pow(post[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0F;
        }

        return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    public float calculateWeightedSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new GeneralException(TradeException.RECOMMENDATION_FAILED);
        }

        float dotProduct = 0.0F;

        // 카테고리
        dotProduct += calculateCategorySimilarity(vectorA, vectorB) * CATEGORY_WEIGHT;

        // 제휴사
        dotProduct += calculatePartnerSimilarity(vectorA, vectorB) * PARTNER_WEIGHT;

        // 가격 유사도 계산
        dotProduct += calculateFigureSimilarity(vectorA[PRICE_INDEX], vectorB[PRICE_INDEX], MAX_PRICE) * PRICE_WEIGHT;

        // 마감기한 유사도 계산
        dotProduct += calculateFigureSimilarity(vectorA[DAYS_TO_EXPIRY_INDEX], vectorB[DAYS_TO_EXPIRY_INDEX], MAX_DAYS_TO_EXPIRY) * DAYS_TO_EXPIRY_WEIGHT;

        return dotProduct;
    }

    public float[] concatenate(float[]... vectors) {
        int totalLength = Arrays.stream(vectors).mapToInt(v -> v.length).sum();
        float[] result = new float[totalLength];

        int index = 0;
        for (float[] vector : vectors) {
            System.arraycopy(vector, 0, result, index, vector.length);
            index += vector.length;
        }
        return result;
    }

    //가격 정규화
    public float normalizePrice(float price) {
        return price / MAX_PRICE;
    }

    //마감기한 정규화
    public float normalizeDaysToExpiry(float days) {
        return days / MAX_DAYS_TO_EXPIRY;
    }
}