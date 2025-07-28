package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveReportResponse {
    private Long reportId;

    public static SaveReportResponse of(final Long reportId) {
        return SaveReportResponse.builder()
                .reportId(reportId)
                .build();
    }
}
