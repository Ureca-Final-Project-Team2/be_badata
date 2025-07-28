package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTrendingResponse {
    private String[] trendingTopics;

    public static GetTrendingResponse of(final String[] trendingTopics) {
        return GetTrendingResponse.builder()
                .trendingTopics(trendingTopics)
                .build();
    }
}