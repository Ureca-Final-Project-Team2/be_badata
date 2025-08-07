package com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector;

import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class VectorUtilsDouble {
    private static final int CATEGORY_COUNT = PostVectorizerDouble.categoryToIndex.size();
    private static final int PARTNER_COUNT = PostVectorizerDouble.partnerToIndex.size();
    private static final int PRICE_INDEX = CATEGORY_COUNT + PARTNER_COUNT;
    private static final int DAYS_TO_EXPIRY_INDEX = PRICE_INDEX + 1;

    // 가중치
    private static final double CATEGORY_WEIGHT = 1.3;
    private static final double PARTNER_WEIGHT = 0.3;
    private static final double PRICE_WEIGHT = 0.9;
    private static final double DAYS_TO_EXPIRY_WEIGHT = 0.5;

    private static final double MAX_PRICE = 30000;
    private static final double MAX_DAYS_TO_EXPIRY = 365;

    public double calculateNumberProximity (double userValue, double postValue, final double maximum) {
        final double difference = Math.abs(userValue - postValue);

        double result = Math.max(0.0F, 1.0F - (difference / maximum));
        return result;
    }

    public double calculateCategorySimilarity (double[] user, double[] post) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            dotProduct += user[i] * post[i];
            normA += Math.pow(user[i], 2);
            normB += Math.pow(post[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double calculatePartnerSimilarity (double[] user, double[] post) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = CATEGORY_COUNT; i < CATEGORY_COUNT + PARTNER_COUNT; i++) {
            dotProduct += user[i] * post[i];
            normA += Math.pow(user[i], 2);
            normB += Math.pow(post[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double calculateWeightedSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new GeneralException(TradeException.RECOMMENDATION_FAILED);
        }

        double dotProduct = 0.0;

        // 카테고리
        dotProduct += calculateCategorySimilarity(vectorA, vectorB) * CATEGORY_WEIGHT;

        // 제휴사
        dotProduct += calculatePartnerSimilarity(vectorA, vectorB) * PARTNER_WEIGHT;

        // 가격 유사도 계산
        dotProduct += calculateNumberProximity(vectorA[PRICE_INDEX], vectorB[PRICE_INDEX], MAX_PRICE) * PRICE_WEIGHT;

        // 마감기한 유사도 계산
        dotProduct += calculateNumberProximity(vectorA[DAYS_TO_EXPIRY_INDEX], vectorB[DAYS_TO_EXPIRY_INDEX], MAX_DAYS_TO_EXPIRY) * DAYS_TO_EXPIRY_WEIGHT;

        return dotProduct;
    }

    public double[] concatenate(double[]... vectors) {
        int totalLength = Arrays.stream(vectors).mapToInt(v -> v.length).sum();
        double[] result = new double[totalLength];

        int index = 0;
        for (double[] vector : vectors) {
            System.arraycopy(vector, 0, result, index, vector.length);
            index += vector.length;
        }
        return result;
    }

    //가격 정규화
    public double normalizePrice(int price) {
        return Math.min(price / MAX_PRICE, 1.0);
    }

    //마감기한 정규화
    public double normalizeDaysToExpiry(int days) {
        return Math.min(days / MAX_DAYS_TO_EXPIRY, 1.0);
    }
}