package com.TwoSeaU.BaData.global.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalException implements BaseException {

    //글로벌 에러 1000번대, 아래 코드는 자유롭게 삭제하세요
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,1000,"서버 예외입니다."),
    NOT_ALLOWABLE_EXTENSION(HttpStatus.BAD_REQUEST, 1001, "지원되지 않는 확장자입니다."),
    INTERNAL_S3_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1002, "S3 관련 서버 에러입니다."),
    INTERNAL_FIREBASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1003, "파이어베이스 관련 서버 에러입니다."),
    INTERNAL_MAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1004, "메일 관련 에러입니다."),
    FIREBASE_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, 1005, "파이어베이스 토큰 만료입니다. 재발급 부탁드립니다."),
    FIREBASE_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, 1006, "파이어베이스 토큰이 유효하지 않습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
