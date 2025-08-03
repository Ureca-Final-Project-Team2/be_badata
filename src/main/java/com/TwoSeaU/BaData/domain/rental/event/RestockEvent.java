package com.TwoSeaU.BaData.domain.rental.event;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class RestockEvent {

    private Reservation reservation;
    private List<DeviceReservation> deviceReservations;

    public static RestockEvent from (final Reservation reservation, final List<DeviceReservation> deviceReservations){

        return RestockEvent.builder()
                .reservation(reservation)
                .deviceReservations(deviceReservations)
                .build();
    }

}
