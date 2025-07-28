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
public class CoinResponse {
	private Integer coin;

	public static CoinResponse of(final Integer coin) {
		return CoinResponse.builder()
			.coin(coin)
			.build();
	}
}
