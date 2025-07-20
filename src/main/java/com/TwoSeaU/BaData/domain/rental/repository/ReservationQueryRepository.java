package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetRentalResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface ReservationQueryRepository {
	CursorPageResponse<GetRentalResponse> getAllRentalsByCursor(final Long cursor, final int size, final Long userId);
}
