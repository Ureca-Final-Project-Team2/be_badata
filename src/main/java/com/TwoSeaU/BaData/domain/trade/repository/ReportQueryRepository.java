package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetReportResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface ReportQueryRepository {

	CursorPageResponse<GetReportResponse> getAllReportsByCursor(final Long cursor, final int size, final Long userId);

}
