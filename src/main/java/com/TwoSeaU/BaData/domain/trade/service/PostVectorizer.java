package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PostVectorizer {
    private final static int CATEGORY_COUNT = 8;
    private final VectorUtils vectorUtils;
    public final static Map<String, Integer> categoryToIndex = new HashMap<>();

    @PostConstruct
    public void initializeMappings() {
        // 카테고리 매핑 초기화
        List<String> categories = Arrays.asList(
                "OTT/뮤직", "도서/아티클", "자기개발", "식품",
                "생활/편의", "패션/뷰티", "키즈", "반려동물");

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            categoryToIndex.put(categories.get(i), i);
        }
    }

    //Post 벡터화
    public double[] vectorizePost(BigDecimal price, LocalDate deadLine, GifticonCategory category) {
        // 카테고리 벡터화
        double[] categoryVector = postCategoryVectorize(category);

        // 수치형 특성 정규화 (가격, 마감 기한)
        double[] numericalFeatures = {
                vectorUtils.normalizePrice(price.intValue()),
                vectorUtils.normalizeDaysToExpiry((int) ChronoUnit.DAYS.between(LocalDate.now(), deadLine))
        };

        // 벡터 결합
        return vectorUtils.concatenate(categoryVector, numericalFeatures);
    }

    private double[] postCategoryVectorize(GifticonCategory category) {
        double[] vector = new double[PostVectorizer.categoryToIndex.size()];

        Integer index = PostVectorizer.categoryToIndex.get(category.getCategoryName());

        if (index != null && index < vector.length) {
            vector[index] = 1.0;
        }

        return vector;
    }
}