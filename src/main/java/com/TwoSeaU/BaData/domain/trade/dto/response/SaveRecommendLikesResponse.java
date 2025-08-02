package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveRecommendLikesResponse {
    private boolean isSaved;

    public static SaveRecommendLikesResponse of(final boolean isSaved) {
        return SaveRecommendLikesResponse.builder()
                .isSaved(isSaved)
                .build();
    }
}
