package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePaymentResponse {
    private String merchantUid;

    public static CreatePaymentResponse of(final String merchantUid) {
        return CreatePaymentResponse.builder()
                .merchantUid(merchantUid)
                .build();
    }
}
