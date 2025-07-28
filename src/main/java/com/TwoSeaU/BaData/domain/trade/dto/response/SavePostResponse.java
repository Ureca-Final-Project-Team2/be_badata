package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SavePostResponse {
    private Long postId;

    public static SavePostResponse of(final Long postId) {
        return SavePostResponse.builder()
                .postId(postId)
                .build();
    }
}
