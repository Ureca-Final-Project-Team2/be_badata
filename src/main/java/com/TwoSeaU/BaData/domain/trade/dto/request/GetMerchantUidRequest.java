package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMerchantUidRequest {
    @NotNull(message = "사용 코인은 필수 입력입니다.")
    @PositiveOrZero(message = "사용 코인은 음수일 수 없습니다.")
    private BigDecimal useCoin;
}
