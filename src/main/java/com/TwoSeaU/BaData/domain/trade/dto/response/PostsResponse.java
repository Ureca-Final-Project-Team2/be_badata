package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostsResponse {
    private List<PostResponse> postsResponse;

    public static PostsResponse of(final List<PostResponse> posts) {
        return PostsResponse.builder()
                .postsResponse(posts)
                .build();
    }
}
