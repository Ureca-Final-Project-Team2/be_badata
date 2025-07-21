package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetPurchaseResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface PaymentQueryRepository {

	CursorPageResponse<GetPurchaseResponse> getAllPurchasesByCursor(final Long cursor, final int size, final Long userId);

}
