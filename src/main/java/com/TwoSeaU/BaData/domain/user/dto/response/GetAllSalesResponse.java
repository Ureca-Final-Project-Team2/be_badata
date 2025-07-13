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
public class GetAllSalesResponse {
	private List<GetSaleResponse> allInProgressPosts;
	private List<GetSaleResponse> allCompletedPosts;
	private List<GetSaleResponse> gifticonInProgressPosts;
	private List<GetSaleResponse> gifticonCompletedPosts;
	private List<GetSaleResponse> dataInProgressPosts;
	private List<GetSaleResponse> dataCompletedPosts;

	public static GetAllSalesResponse of(
		final List<GetSaleResponse> allInProgressPosts, final List<GetSaleResponse> allCompletedPosts,
		final List<GetSaleResponse> gifticonInProgressPosts, final List<GetSaleResponse> gifticonCompletedPosts,
		final List<GetSaleResponse> dataInProgressPosts, final List<GetSaleResponse> dataCompletedPosts) {
		return GetAllSalesResponse.builder()
			.allInProgressPosts(allInProgressPosts)
			.allCompletedPosts(allCompletedPosts)
			.gifticonInProgressPosts(gifticonInProgressPosts)
			.gifticonCompletedPosts(gifticonCompletedPosts)
			.dataInProgressPosts(dataInProgressPosts)
			.dataCompletedPosts(dataCompletedPosts)
			.build();
	}
}
