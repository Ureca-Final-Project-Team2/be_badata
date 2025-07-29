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
public class GetUserInfoResponse {

	private String profileImage;
	private String nickName;
	private Long days;

	public static GetUserInfoResponse from(final User user, final Long days) {
		return GetUserInfoResponse.builder()
			.profileImage(user.getProfileImageUrl())
			.nickName(user.getNickName())
			.days(days)
			.build();
	}
}
