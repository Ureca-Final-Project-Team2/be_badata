package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;

public interface PostQueryRepository {

	CursorPageResponse<GetSaleResponse> getAllSalesByCursor(PostCategory postCategory, Boolean isSold, Long cursor, int size, Long userId);
    CursorPageResponse<PostResponse> searchPostsByKeyword(String keyword, String username, Long cursor, int size);
    CursorPageResponse<PostResponse> searchPostsByUserAndIsSold(Long userId, boolean isSold, String username, Long cursor, int size);

    CursorPageResponse<PostResponse> searchPostsByDeadLine(String username, Long cursor, int size);
}
