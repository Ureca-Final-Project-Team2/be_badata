package com.TwoSeaU.BaData.domain.rental.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RentalException implements BaseException {

    ALREADY_RENTAL_EXIST_SAME_PERIOD(HttpStatus.BAD_REQUEST, 4002, "이미 해당 기간에 예약이 존재합니다."),
    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, 4003, "해당 예약을 찾을 수 없습니다."),
    CANT_CANCEL_ALREADY_RENTAL(HttpStatus.BAD_REQUEST, 4004, "이미 빌리거나 이미 사용한 경우 예약을 취소할 수 없습니다."),
    CANT_CANCEL_RESERVED_USERS(HttpStatus.BAD_REQUEST, 4005, "예약을 진행한 당사자만 예약을 취소할 수 있습니다."),
    CANT_RESTOCK_WHEN_AVAILABLE_COUNT(HttpStatus.BAD_REQUEST, 4006, "예약 가능한 상황일 때는 재입고를 신청할 수 없습니다"),
    CANT_RESERVE_MORE_THAN_COUNT(HttpStatus.BAD_REQUEST, 4007, "예약 가능 대수는 가맹점이 소유한 기기 이상으로 할 수 없습니다"),
    CANT_RESTOCK_MORE_THAN_COUNT(HttpStatus.BAD_REQUEST, 4008, "재입고 알림 대수는 가맹점이 소유한 기기 이상으로 할 수 없습니다");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
