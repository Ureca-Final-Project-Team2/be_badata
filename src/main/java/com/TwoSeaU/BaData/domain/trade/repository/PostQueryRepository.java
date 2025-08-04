package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

import java.util.List;
import java.util.Set;

public interface PostQueryRepository {

	CursorPageResponse<GetSaleResponse> getAllSalesByCursor(final PostCategory postCategory, final Boolean isSold, final Long cursor, final int size, final Long userId);
    CursorPageResponse<PostResponse> searchPostsByKeyword(final String keyword, final String username, final Long cursor, final int size);
    CursorPageResponse<PostResponse> searchPostsByUserAndIsSold(final Long userId, final boolean isSold, final String username, final Long cursor, final int size);

    CursorPageResponse<PostResponse> searchPostsByDeadLine(final String username, final Long cursor, final int size);

    List<Post> getRecentPostsBySize(final int size, final Set<Long> excludedPostIds);
}
