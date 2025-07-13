package com.TwoSeaU.BaData.domain.trade.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePostRequest {
    private String comment;
    private Integer price;
}