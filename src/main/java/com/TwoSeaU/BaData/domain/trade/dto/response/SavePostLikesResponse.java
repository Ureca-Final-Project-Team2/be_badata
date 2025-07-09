package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SavePostLikesResponse {
    private Long likeId;

    public static SavePostLikesResponse of(final Long likeId) {
        return SavePostLikesResponse.builder()
                .likeId(likeId)
                .build();
    }
}
