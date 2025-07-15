package com.TwoSeaU.BaData.domain.sos.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveSosResponse {
	private Long sosId;

	public static SaveSosResponse of(Long sosId) {
		return SaveSosResponse.builder()
			.sosId(sosId)
			.build();
	}
}
