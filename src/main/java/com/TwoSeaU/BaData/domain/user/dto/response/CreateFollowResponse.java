package com.TwoSeaU.BaData.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateFollowResponse {

    private boolean isFollowing;

    public static CreateFollowResponse of(final boolean isFollowing){

        return CreateFollowResponse.builder()
                .isFollowing(isFollowing)
                .build();
    }

}
