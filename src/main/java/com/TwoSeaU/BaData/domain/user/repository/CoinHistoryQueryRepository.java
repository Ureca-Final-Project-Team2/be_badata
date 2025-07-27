package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetCoinHistoryResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface CoinHistoryQueryRepository {
	CursorPageResponse<GetCoinHistoryResponse> getAllCoinsResponse(final Long cursor, final int size, final Long userId);
}
