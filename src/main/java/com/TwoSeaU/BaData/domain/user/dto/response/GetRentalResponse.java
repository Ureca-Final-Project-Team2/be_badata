package com.TwoSeaU.BaData.domain.user.dto.response;

import java.time.LocalDateTime;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;
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
public class GetRentalResponse {
	private Long id;
	private String storeName;
	private LocalDateTime rentalStartDate;
	private Integer price;
	private Boolean isReviewed;
	private ReservationStatus reservationStatus;

	public static GetRentalResponse from(final Reservation reservation, final Store store, final Boolean isReviewed) {
		return GetRentalResponse.builder()
			.id(reservation.getId())
			.storeName(store.getName())
			.rentalStartDate(reservation.getRentalStartDate())
			.price(reservation.getPrice())
			.isReviewed(isReviewed)
			.reservationStatus(reservation.getStatus())
			.build();
	}
}
