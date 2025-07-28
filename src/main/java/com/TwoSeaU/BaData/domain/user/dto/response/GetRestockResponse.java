package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRestockResponse {
	private Long id;
	private String deviceImage;
	private Integer price;
	private String deviceName;
	private Boolean is5G;

	public static GetRestockResponse from(final ReStock reStock, final StoreDevice storeDevice, final Device device) {
		return GetRestockResponse.builder()
			.id(reStock.getId())
			.deviceImage(device.getImageUrl())
			.deviceName(device.getName())
			.price(storeDevice.getPrice())
			.is5G(device.getIs5G())
			.build();
	}
}
