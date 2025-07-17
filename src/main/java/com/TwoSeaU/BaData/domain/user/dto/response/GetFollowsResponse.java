package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFollowsResponse {
	private Long id;
	private Long userId;
	private String username;
	private String profileImageUrl;

	public static GetFollowsResponse from(final User user, final Long userLikesId) {
		return GetFollowsResponse.builder()
			.id(userLikesId)
			.userId(user.getId())
			.username(user.getNickName())
			.profileImageUrl(user.getProfileImageUrl())
			.build();
	}
}

