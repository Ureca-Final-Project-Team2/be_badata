package com.TwoSeaU.BaData.domain.trade.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPostsResponse {
    private PostsResponse soldingPostsResponse;
    private PostsResponse soldedPostsResponse;

    public static UserPostsResponse of(final PostsResponse soldingPosts, final PostsResponse soldedPosts) {

        return UserPostsResponse.builder()
                .soldingPostsResponse(soldingPosts)
                .soldedPostsResponse(soldedPosts)
                .build();
    }
}