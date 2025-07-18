package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePostRequest {
    @NotNull(message = "상세 설명은 비워둘 수 없습니다.")
    private String comment;

    @NotNull(message = "가격은 비워둘 수 없습니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    private Integer price;
}