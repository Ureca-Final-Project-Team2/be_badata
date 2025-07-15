package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.sos.entity.Sos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetSosResponse {
	private Long sosId;
	private Long responderId;
	private LocalDateTime createdAt;
	private Boolean isSuccess;

	public static GetSosResponse from(final Sos sos) {
		return GetSosResponse.builder()
			.sosId(sos.getId())
			.responderId(sos.getResponder() != null ? sos.getResponder().getId() : null)
			.createdAt(sos.getCreatedAt())
			.isSuccess(sos.getResponder() != null)
			.build();
	}
}
