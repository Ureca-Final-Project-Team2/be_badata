package com.TwoSeaU.BaData.domain.sos.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RespondSosResponse {
	private Long sosId;
	private Boolean isSuccess;

	public static RespondSosResponse of(final Long sosId, final Boolean isSuccess) {
		return RespondSosResponse.builder()
			.sosId(sosId)
			.isSuccess(isSuccess)
			.build();
	}
}
