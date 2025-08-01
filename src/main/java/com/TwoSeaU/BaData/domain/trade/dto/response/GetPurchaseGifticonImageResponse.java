package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPurchaseGifticonImageResponse {
    private String postImage;
    private String couponNumber;

    public static GetPurchaseGifticonImageResponse of(final String postImage, final String couponNumber) {
        return GetPurchaseGifticonImageResponse.builder()
                .postImage(postImage)
                .couponNumber(couponNumber)
                .build();
    }
}
