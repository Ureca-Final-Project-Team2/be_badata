package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
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
public class GetReportResponse {
	private Long id;
	private Long postId;
	private ReportStatus reportStatus;
	private ReportType reportTypeCode;
	private String reportReason;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static GetReportResponse from(final Report report, final Post post) {
		return GetReportResponse.builder()
			.id(report.getId())
			.postId(post.getId())
			.reportStatus(report.getReportStatus())
			.reportTypeCode(report.getReportTypeCode())
			.reportReason(report.getReportReason())
			.createdAt(report.getCreatedAt())
			.updatedAt(report.getUpdatedAt())
			.build();
	}
}
