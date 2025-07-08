package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPostsResponse {
    private List<PostResponse> soldingPostsResponse;
    private List<PostResponse> soldedPostsResponse;

    public static UserPostsResponse of(final List<PostResponse> soldingPosts, final List<PostResponse> soldedPosts) {
        return UserPostsResponse.builder()
                .soldingPostsResponse(soldingPosts)
                .soldedPostsResponse(soldedPosts)
                .build();
    }
}