package com.TwoSeaU.BaData.domain.rental.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RentalException implements BaseException {

    ALREADY_RENTAL_EXIST_SAME_PERIOD(HttpStatus.BAD_REQUEST, 4002, "이미 해당 기간에 예약이 존재합니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
