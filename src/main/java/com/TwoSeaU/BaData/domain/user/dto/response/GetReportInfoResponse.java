package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReportInfoResponse {

	private LocalDateTime paymentDateTime;
	private LocalDateTime questionDateTime;
	private ReportStatus reportStatus;
	private ReportType reportType;

	public static GetReportInfoResponse from(final Report report, final LocalDateTime paymentDateTime) {
		return GetReportInfoResponse.builder()
			.paymentDateTime(paymentDateTime)
			.questionDateTime(report.getCreatedAt())
			.reportStatus(report.getReportStatus())
			.reportType(report.getReportTypeCode())
			.build();
	}

}
