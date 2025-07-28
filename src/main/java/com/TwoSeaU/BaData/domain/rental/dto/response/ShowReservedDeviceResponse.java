package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowReservedDeviceResponse {

    private String deviceName;
    private int dataCapacity;
    private int price;
    private int count;

    public static ShowReservedDeviceResponse from(final StoreDevice storeDevice,final
            DeviceReservation deviceReservation, final Reservation reservation){

        long daysBetween = ChronoUnit.DAYS.between(reservation.getRentalStartDate(), reservation.getRentalEndDate());
        int days = Math.max(1, (int) daysBetween);

        return ShowReservedDeviceResponse.builder()
                .deviceName(storeDevice.getDevice().getName())
                .dataCapacity(storeDevice.getDataCapacity())
                .price(storeDevice.getPrice() * days)
                .count(deviceReservation.getReservationCount())
                .build();
    }

}
