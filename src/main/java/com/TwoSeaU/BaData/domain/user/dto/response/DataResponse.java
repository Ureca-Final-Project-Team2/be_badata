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
public class DataResponse {

	private Integer dataAmount;

	public static DataResponse of(final Integer dataAmount) {
		return DataResponse.builder()
			.dataAmount(dataAmount)
			.build();
	}
}