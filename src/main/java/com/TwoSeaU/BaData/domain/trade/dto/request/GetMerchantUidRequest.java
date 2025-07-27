package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMerchantUidRequest {
    @NotNull(message = "사용 코인은 필수 입력입니다.")
    @PositiveOrZero(message = "사용 코인은 음수일 수 없습니다.")
    private BigDecimal useCoin;
}
