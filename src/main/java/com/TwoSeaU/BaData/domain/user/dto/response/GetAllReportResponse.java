package com.TwoSeaU.BaData.domain.user.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAllReportResponse {
	private List<GetReportResponse> reportList;

	public static GetAllReportResponse of(final List<GetReportResponse> reportList) {
		return GetAllReportResponse.builder()
			.reportList(reportList)
			.build();
	}
}
