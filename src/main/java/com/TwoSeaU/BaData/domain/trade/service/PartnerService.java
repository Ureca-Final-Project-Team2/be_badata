package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetPartnerResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Partner;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonCategoryRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PartnerRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;

    public GetPartnerResponse getPartners(Long categoryId) {
        if(!gifticonCategoryRepository.existsById(categoryId)) {
            throw new GeneralException(TradeException.NOT_FOUND_GIFTICON_CATEGORY);
        }

        List<Partner> partners = partnerRepository.findByCategoryId(categoryId);

        return GetPartnerResponse.from(partners);
    }
}
