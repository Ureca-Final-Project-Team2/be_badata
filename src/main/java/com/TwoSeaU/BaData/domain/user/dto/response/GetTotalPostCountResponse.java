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
public class GetTotalPostCountResponse {
	private Integer postCount;

	public static GetTotalPostCountResponse of(final Integer postCount) {
		return GetTotalPostCountResponse.builder()
			.postCount(postCount)
			.build();
	}
}
