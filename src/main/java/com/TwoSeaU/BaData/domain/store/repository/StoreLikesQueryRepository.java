package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesStoreResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import java.util.Set;

public interface StoreLikesQueryRepository {

	CursorPageResponse<GetLikesStoreResponse> getAllLikesStoresResponseByCursor(final Long cursor, final int size, final Long userId);

	Set<Long> getUserLikedStoreIds(final String username);

}
