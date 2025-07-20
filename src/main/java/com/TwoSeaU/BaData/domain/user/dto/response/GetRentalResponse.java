package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRentalResponse {
	private Long id;
	private String storeName;
	private LocalDateTime rentalStartDate;
	private Integer price;
	private ReservationStatus reservationStatus;

	public static GetRentalResponse from(final Reservation reservation) {
		return GetRentalResponse.builder()
			.id(reservation.getId())
			.storeName(reservation.getStore().getName())
			.rentalStartDate(reservation.getRentalStartDate())
			.price(reservation.getPrice())
			.reservationStatus(reservation.getStatus())
			.build();
	}
}
