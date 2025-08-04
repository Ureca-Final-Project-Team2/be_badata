package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateGifticonPostRequest {
    private String comment;

    @NotNull(message = "가격은 비워둘 수 없습니다.")
    private BigDecimal price;
}