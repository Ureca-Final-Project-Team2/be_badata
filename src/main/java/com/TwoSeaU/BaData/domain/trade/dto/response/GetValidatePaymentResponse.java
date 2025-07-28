package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetValidatePaymentResponse {
    private Long paymentId;

    public static GetValidatePaymentResponse of(final Long paymentId) {
        return GetValidatePaymentResponse.builder()
                .paymentId(paymentId)
                .build();
    }
}
