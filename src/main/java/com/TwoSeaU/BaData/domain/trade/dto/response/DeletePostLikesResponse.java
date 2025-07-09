package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletePostLikesResponse {
    private Long likeId;

    public static DeletePostLikesResponse of(final Long likeId) {
        return DeletePostLikesResponse.builder()
                .likeId(likeId)
                .build();
    }
}
