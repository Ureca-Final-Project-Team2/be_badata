package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

import java.util.List;

public interface PostLikesQueryRepository {
	CursorPageResponse<GetLikesPostResponse> getAllLikesPostsByCursor(final Long cursor, final int size, final Long userId);

	List<Long> findDistinctPostIdsByUserId(final Long userId);
}
