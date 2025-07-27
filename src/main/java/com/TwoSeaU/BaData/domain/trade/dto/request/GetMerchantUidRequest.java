package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMerchantUidRequest {
    @NotNull(message = "사용 코인은 필수 입력입니다.")
    private BigDecimal useCoin;
}
