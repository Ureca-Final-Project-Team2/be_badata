package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletePostResponse {
    private Long postId;

    public static DeletePostResponse of(final Long postId) {
        return DeletePostResponse.builder()
                .postId(postId)
                .build();
    }
}
