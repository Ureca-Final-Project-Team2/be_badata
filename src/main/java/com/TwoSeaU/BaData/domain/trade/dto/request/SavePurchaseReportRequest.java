package com.TwoSeaU.BaData.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SavePurchaseReportRequest {
    @NotNull(message = "신고 사유는 필수 작성입니다.")
    private String comment;
}
