package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalTime;

import com.TwoSeaU.BaData.domain.store.entity.Store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetLikesStoreResponse {
	private Long id;
	private Long storeId;
	private String name;
	private Integer availableDevice;
	private String storeImage;
	private LocalTime startTime;
	private LocalTime endTime;
	private String detailAddress;

	public static GetLikesStoreResponse from(final Long likesStoreId, final Store store) {
		return GetLikesStoreResponse.builder()
			.id(likesStoreId)
			.storeId(store.getId())
			.name(store.getName())
			.availableDevice(store.getAvailableDevice())
			.storeImage(store.getStoreImage())
			.startTime(store.getStartTime())
			.endTime(store.getEndTime())
			.detailAddress(store.getDetailAddress())
			.build();
	}
}