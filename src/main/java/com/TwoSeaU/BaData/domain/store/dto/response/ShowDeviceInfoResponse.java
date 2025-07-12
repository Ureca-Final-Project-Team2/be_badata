package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowDeviceInfoResponse {

    private Long storeDeviceId;
    private String deviceName;
    private int dataCapacity;
    private String imageUrl;

    public static ShowDeviceInfoResponse from(final StoreDevice storeDevice){

        return ShowDeviceInfoResponse.builder()
                .dataCapacity(storeDevice.getDataCapacity())
                .storeDeviceId(storeDevice.getId())
                .deviceName(storeDevice.getDevice().getName())
                .imageUrl(storeDevice.getDevice().getImageUrl())
                .build();
    }

}
