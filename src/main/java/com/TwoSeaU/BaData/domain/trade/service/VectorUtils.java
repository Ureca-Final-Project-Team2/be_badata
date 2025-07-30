package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class VectorUtils {
    private static final int CATEGORY_COUNT = 8;

    // 가중치
    private static final double CATEGORY_WEIGHT = 1.6;
    private static final double PRICE_WEIGHT = 0.9;
    private static final double DAYS_TO_EXPIRY_WEIGHT = 0.5;

    public static double calculateNumberProximity (double userPrice, double postPrice) {
        double priceDiff = Math.abs(userPrice - postPrice);
        return 1.0 / (1.0 + priceDiff / 1000.0);
    }

    public static double calculateCategorySimilarity (double[] user, double[] post) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            dotProduct += user[i] * post[i] * CATEGORY_WEIGHT;
            normA += Math.pow(user[i], 2);
            normB += Math.pow(post[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new GeneralException(TradeException.RECOMMENDATION_FAILED);
        }

        double dotProduct = 0.0;

        // 카테고리
        dotProduct += calculateCategorySimilarity(vectorA, vectorB);

        // 가격 유사도 계산
        dotProduct += calculateNumberProximity(vectorA[CATEGORY_COUNT], vectorB[CATEGORY_COUNT]) * PRICE_WEIGHT;

        // 마감기한 유사도 계산
        dotProduct += calculateNumberProximity(vectorA[CATEGORY_COUNT + 1], vectorB[CATEGORY_COUNT + 1]) * DAYS_TO_EXPIRY_WEIGHT;

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
        return Math.min(price / 50000.0, 1.0);
    }

    //마감기한 정규화
    public double normalizeDaysToExpiry(int days) {
        return Math.min(days / 365.0, 1.0);
    }
}