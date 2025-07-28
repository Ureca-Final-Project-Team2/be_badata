package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePaymentResponse {
    private String merchantUid;
    private BigDecimal amount;

    public static CreatePaymentResponse of(final String merchantUid, final BigDecimal amount) {
        return CreatePaymentResponse.builder()
                .merchantUid(merchantUid)
                .amount(amount)
                .build();
    }
}
