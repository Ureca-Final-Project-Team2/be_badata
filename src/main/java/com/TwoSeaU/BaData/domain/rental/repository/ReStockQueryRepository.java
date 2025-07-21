package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetRestockResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface ReStockQueryRepository {
	CursorPageResponse<GetRestockResponse> getAllRestocksByCursor(final Long cursor, final int size, final Long userId);
}
