package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReportResponse {
	private Long reportId;
	private Long postId;
	private ReportStatus reportStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static GetReportResponse from(final Report report) {
		return GetReportResponse.builder()
			.reportId(report.getId())
			.postId(report.getPost().getId())
			.reportStatus(report.getReportStatus())
			.createdAt(report.getCreatedAt())
			.updatedAt(report.getUpdatedAt())
			.build();
	}
}
