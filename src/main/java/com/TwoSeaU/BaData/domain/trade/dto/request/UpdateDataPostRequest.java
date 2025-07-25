package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateDataPostRequest {
    @NotNull(message = "제목은 필수 작성입니다.")
    private String title;

    private String comment;

    @NotNull(message = "가격은 비워둘 수 없습니다.")
    private Integer price;
}
