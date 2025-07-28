package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.user.entity.PlanData;
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
public class GetDataResponse {

	private String nickname;
	private String planName;
	private Integer totalDataAmount;
	private Integer dataAmount;

	public static GetDataResponse from(final User user, final PlanData planData) {
		return GetDataResponse.builder()
			.nickname(user.getNickName())
			.planName(planData.getPlanName())
			.totalDataAmount(planData.getDataAmount())
			.dataAmount(user.getDataAmount())
			.build();
	}
}