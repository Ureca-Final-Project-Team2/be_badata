package com.TwoSeaU.BaData.domain.sos.dto.response;

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
public class ConnectSseResponse {
	private Long sosId;
	private Long requesterId;
	private Long responderId;
	private String type;
	private LocalDateTime createdAt;

	public static ConnectSseResponse of(final Sos sos, final String type) {
		return ConnectSseResponse.builder()
			.sosId(sos.getId())
			.requesterId(sos.getRequester().getId())
			.responderId(sos.getResponder() != null ? sos.getResponder().getId() : null)
			.type(type)
			.createdAt(sos.getCreatedAt())
			.build();
	}
}
