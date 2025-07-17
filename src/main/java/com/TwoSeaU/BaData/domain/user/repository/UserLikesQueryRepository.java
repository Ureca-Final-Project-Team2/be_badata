package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetFollowsResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface UserLikesQueryRepository {
	CursorPageResponse<GetFollowsResponse> getAllFollowersResponse(Long cursor, int size, Long userId);
	CursorPageResponse<GetFollowsResponse> getAllFollowingsResponse(Long cursor, int size, Long userId);
}
