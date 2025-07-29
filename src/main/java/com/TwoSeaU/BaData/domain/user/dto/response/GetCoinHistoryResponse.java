package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;
import com.TwoSeaU.BaData.domain.user.enums.CoinSource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCoinHistoryResponse {
	private Long id;
	private Integer usedCoin;
	private CoinSource coinSource;
	private Integer totalCoin;
	private LocalDateTime createdAt;

	public static GetCoinHistoryResponse of(final CoinHistory coinHistory) {
		return GetCoinHistoryResponse.builder()
			.id(coinHistory.getId())
			.usedCoin(coinHistory.getAmount())
			.coinSource(coinHistory.getCoinSource())
			.totalCoin(coinHistory.getTotalAmount())
			.createdAt(coinHistory.getCreatedAt())
			.build();
	}
}