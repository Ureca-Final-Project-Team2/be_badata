package com.TwoSeaU.BaData.global.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalException implements BaseException {
    //글로벌 에러 1000번대, 아래 코드는 자유롭게 삭제하세요
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,1000,"서버 예외입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
