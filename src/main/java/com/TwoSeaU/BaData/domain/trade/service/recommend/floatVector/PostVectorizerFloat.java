package com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector;

import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Partner;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonCategoryRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PartnerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostVectorizerFloat {
    // 가중치
    private static final float CATEGORY_WEIGHT = 1.3F;
    private static final float PARTNER_WEIGHT = 0.3F;
    private static final float PRICE_WEIGHT = 0.9F;
    private static final float DAYS_TO_EXPIRY_WEIGHT = 0.5F;

    private final VectorUtilsFloat vectorUtilsFloat;
    private final PartnerRepository partnerRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;

    public final static Map<String, Integer> categoryToIndex = new HashMap<>();
    public final static Map<String, Integer> partnerToIndex = new HashMap<>();

    @PostConstruct
    public void initializeMappings() {
        // 카테고리 매핑 초기화
        final List<String> categories = gifticonCategoryRepository.findAll(Sort.by("id")).stream()
                .map(GifticonCategory::getCategoryName)
                .toList();

        for (int i = 0; i < categories.size(); i++) {
            categoryToIndex.put(categories.get(i), i);
        }

        // 제휴사 매핑 초기화
        final List<String> partners = partnerRepository.findAll(Sort.by("id")).stream()
                .map(Partner::getPartner)
                .toList();

        for (int i = 0; i < partners.size(); i++) {
            partnerToIndex.put(partners.get(i), i);
        }
    }

    //Post 벡터화
    public float[] vectorizePost(final BigDecimal price, final LocalDate deadLine, final GifticonCategory category, final String partner) {
        // 카테고리 벡터화
        final float[] categoryVector = postCategoryVectorize(category);

        // 제휴사 벡터화
        final float[] partnerVector = postPartnerVectorize(partner);

        // 수치형 특성 정규화 (가격, 마감 기한)
        final float[] numericalFeatures = {
                vectorUtilsFloat.normalizePrice(price.intValue() * PRICE_WEIGHT),
                vectorUtilsFloat.normalizeDaysToExpiry(ChronoUnit.DAYS.between(LocalDate.now(), deadLine) * DAYS_TO_EXPIRY_WEIGHT)
        };

        // 벡터 결합
        return vectorUtilsFloat.concatenate(categoryVector, partnerVector, numericalFeatures);
    }

    private float[] postCategoryVectorize(GifticonCategory category) {
        final float[] vector = new float[categoryToIndex.size()];

        final Integer index = categoryToIndex.get(category.getCategoryName());

        if (index != null && index < vector.length) {
            vector[index] = CATEGORY_WEIGHT;
        }

        return vector;
    }

    private float[] postPartnerVectorize(String partner) {
        final float[] vector = new float[partnerToIndex.size()];

        final Integer index = partnerToIndex.get(partner);

        if (index != null && index < vector.length) {
            vector[index] = PARTNER_WEIGHT;
        }

        return vector;
    }
}