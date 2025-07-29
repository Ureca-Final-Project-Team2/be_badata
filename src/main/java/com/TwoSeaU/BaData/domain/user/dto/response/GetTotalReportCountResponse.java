package com.TwoSeaU.BaData.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTotalReportCountResponse {
	private Long questionCount;
	private Long answerCount;
	private Long completeCount;

	public static GetTotalReportCountResponse of(final Long questionCount, final Long answerCount, final Long completeCount) {
		return GetTotalReportCountResponse.builder()
			.questionCount(questionCount)
			.answerCount(answerCount)
			.completeCount(completeCount)
			.build();
	}
}
