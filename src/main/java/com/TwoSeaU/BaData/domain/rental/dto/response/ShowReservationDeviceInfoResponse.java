package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.dto.projection.AvailableDeviceProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowReservationDeviceInfoResponse {

    private Long storeDeviceId;
    private Long deviceId;
    private String deviceName;
    private int dataCapacity;
    private String imageUrl;
    private Integer availableCount;
    private Integer totalCount;
    private Integer price;

    public static ShowReservationDeviceInfoResponse from(final AvailableDeviceProjection availableDeviceProjection){

        return ShowReservationDeviceInfoResponse.builder()
                .storeDeviceId(availableDeviceProjection.getStoreDeviceId())
                .deviceId(availableDeviceProjection.getDeviceId())
                .deviceName(availableDeviceProjection.getDeviceName())
                .dataCapacity(availableDeviceProjection.getDataCapacity())
                .imageUrl(availableDeviceProjection.getImageUrl())
                .availableCount(availableDeviceProjection.getAvailableCount())
                .price(availableDeviceProjection.getPrice())
                .totalCount(availableDeviceProjection.getTotalCount())
                .build();
    }

}
