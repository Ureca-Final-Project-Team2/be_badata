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
public class UpdateNotificationSettingResponse {

	private Boolean isNotificationEnabled;

	public static UpdateNotificationSettingResponse from(final User user) {
		return UpdateNotificationSettingResponse.builder()
			.isNotificationEnabled(user.getIsNotificationEnabled())
			.build();
	}
}
