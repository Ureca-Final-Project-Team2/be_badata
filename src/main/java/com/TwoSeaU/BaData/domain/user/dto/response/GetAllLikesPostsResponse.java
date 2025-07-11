package com.TwoSeaU.BaData.domain.user.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAllLikesPostsResponse {
	private List<GetLikesPostResponse> getLikesPostResponseList;

	public static GetAllLikesPostsResponse of(List<GetLikesPostResponse> getLikesPostResponseList) {
		return GetAllLikesPostsResponse.builder()
			.getLikesPostResponseList(getLikesPostResponseList)
			.build();
	}
}
