package com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector;

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
public class PostVectorizerDouble {
    private final VectorUtilsDouble vectorUtils;
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
    public double[] vectorizePost(final BigDecimal price, final LocalDate deadLine, final GifticonCategory category, final String partner) {
        // 카테고리 벡터화
        final double[] categoryVector = postCategoryVectorize(category);

        // 제휴사 벡터화
        final double[] partnerVector = postPartnerVectorize(partner);

        // 수치형 특성 정규화 (가격, 마감 기한)
        final double[] numericalFeatures = {
                price.intValue(),
                vectorUtils.normalizeDaysToExpiry((int) ChronoUnit.DAYS.between(LocalDate.now(), deadLine))
        };

        // 벡터 결합
        return vectorUtils.concatenate(categoryVector, partnerVector, numericalFeatures);
    }

    private double[] postCategoryVectorize(GifticonCategory category) {
        final double[] vector = new double[categoryToIndex.size()];

        final Integer index = categoryToIndex.get(category.getCategoryName());

        if (index != null && index < vector.length) {
            vector[index] = 1.0;
        }

        return vector;
    }

    private double[] postPartnerVectorize(String partner) {
        final double[] vector = new double[partnerToIndex.size()];

        final Integer index = partnerToIndex.get(partner);

        if (index != null && index < vector.length) {
            vector[index] = 1.0;
        }

        return vector;
    }
}