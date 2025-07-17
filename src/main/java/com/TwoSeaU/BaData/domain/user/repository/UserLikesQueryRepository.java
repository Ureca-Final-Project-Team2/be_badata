package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetFollowsResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface UserLikesQueryRepository {
	CursorPageResponse<GetFollowsResponse> getAllFollowersResponse(final Long cursor, final int size, final Long userId);
	CursorPageResponse<GetFollowsResponse> getAllFollowingsResponse(final Long cursor, final int size, final Long userId);
}
