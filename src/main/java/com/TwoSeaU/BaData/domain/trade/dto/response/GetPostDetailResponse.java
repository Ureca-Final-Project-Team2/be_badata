package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPostDetailResponse<T> {
    private GetSellerResponse seller;
    private T post;

    public static GetPostDetailResponse of(final GetSellerResponse seller, final Object post) {
        return GetPostDetailResponse.builder()
                .seller(seller)
                .post(post)
                .build();
    }
}
