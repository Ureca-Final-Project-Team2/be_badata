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
    CANT_RESTOCK_WHEN_AVAILABLE_COUNT(HttpStatus.BAD_REQUEST, 4006, "예약 가능한 상황일 때는 재입고를 신청할 수 없습니다."),
    CANT_RESTOCK_MORE_THAN_COUNT(HttpStatus.BAD_REQUEST, 4007, "재입고 알림 대수는 가맹점이 소유한 기기 이상으로 할 수 없습니다."),
    CANT_END_DATE_BEFORE_THAN_START_DATE(HttpStatus.BAD_REQUEST, 4008, "예약 재입고 시 시작 날짜는 종료 날짜보다 먼저 와야 합니다."),
    DONT_MATCH_STORE_DEVICE_STORE(HttpStatus.BAD_REQUEST,4009,"요청된 장치가 해당 가맹점에 속하지 않습니다."),
    DONT_MATCH_REVIEW_RESERVATION_OWNER(HttpStatus.BAD_REQUEST,4010,"리뷰 요청 주체가 예약을 진행한 주체가 아닙니다."),
    CANT_FIND_QUICK_REPLY(HttpStatus.NOT_FOUND, 4011, "해당 퀵 리플라이를 찾을 수 없습니다."),
    CANT_ACCESS_TO_OTHER_RESERVATION(HttpStatus.FORBIDDEN, 4012, "다른 사람의 예약 정보는 접근할 수 없습니다."),
    CANT_WRITE_REVIEW_IN_SAME_RESERVATION(HttpStatus.BAD_REQUEST, 4013, "이미 해당 예약에 리뷰를 작성하였습니다."),
    CANT_ACCESS_TO_OTHER_REVIEW(HttpStatus.FORBIDDEN, 4014, "다른 사람의 리뷰에는 접근할 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND , 4015, "해당 리뷰를 찾을 수 없습니다."),
    CANT_RESERVATION_MORE_THAN_COUNT(HttpStatus.BAD_REQUEST , 4016, "예약은 해당 가맹점이 소유한 기기 이상으로 수행할 수 없습니다."),
    CANT_FIND_RESTOCK(HttpStatus.NOT_FOUND, 4017, "해당 재입고 알림을 찾을 수 없습니다."),
    CANT_DELETE_OTHER_RESTOCK(HttpStatus.BAD_REQUEST, 4018, "다른 사람의 재입고 알림은 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
