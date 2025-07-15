package com.TwoSeaU.BaData.domain.sos.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetSosResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface SosQueryRepository {
	CursorPageResponse<GetSosResponse> getAllSosResponse(Long cursor, int size, Long userId);
}
