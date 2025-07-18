package com.TwoSeaU.BaData.domain.trade.dto.request;

import com.TwoSeaU.BaData.domain.trade.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveReportRequest {
    @NotNull(message = "신고 사유는 필수 선택입니다.")
    private ReportType reportType;

    private String comment;
}
