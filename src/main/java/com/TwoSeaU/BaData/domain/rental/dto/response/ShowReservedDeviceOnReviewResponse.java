package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
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
public class ShowReservedDeviceOnReviewResponse {

    private String deviceName;
    private int dataCapacity;
    private int count;

    public static ShowReservedDeviceOnReviewResponse from(final DeviceReservation deviceReservation){

        return ShowReservedDeviceOnReviewResponse
                .builder()
                .deviceName(deviceReservation.getStoreDevice().getDevice().getName())
                .dataCapacity(deviceReservation.getStoreDevice().getDataCapacity())
                .count(deviceReservation.getReservationCount())
                .build();
    }

}
