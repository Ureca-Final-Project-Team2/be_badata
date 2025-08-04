package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostLikesQueryRepository {
	CursorPageResponse<GetLikesPostResponse> getAllLikesPostsByCursor(final Long cursor, final int size, final Long userId);

	List<Long> findDistinctPostIdsByUserId(final Long userId);

    Map<Long, Integer> countByPostIds(List<Long> postIds);

	Set<Long> findLikedPostIdsByUserIdAndPostIds(Long userId, List<Long> postIds);
}
