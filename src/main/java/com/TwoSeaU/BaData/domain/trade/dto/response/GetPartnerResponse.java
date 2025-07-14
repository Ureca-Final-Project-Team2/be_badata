package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Partner;
import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPartnerResponse {
    private List<String> partnerNames;

    public static GetPartnerResponse from(List<Partner> partners) {
        return GetPartnerResponse.builder()
                .partnerNames(partners.stream()
                        .map(Partner::getPartner)
                        .toList())
                .build();
    }
}
