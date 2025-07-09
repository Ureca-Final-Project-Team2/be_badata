package com.TwoSeaU.BaData.domain.rental.enums;


import lombok.Getter;

@Getter
public enum ReservationStatus {

    PENDING("수령 전"),BURROWING("대여 중"),COMPLETE("완료");

    private final String value;
    ReservationStatus(String value) {
        this.value = value;
    }
}
