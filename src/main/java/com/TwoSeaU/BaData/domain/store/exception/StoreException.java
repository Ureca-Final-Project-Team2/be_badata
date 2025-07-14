package com.TwoSeaU.BaData.domain.store.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreException implements BaseException {

    CANT_FIND_STORE(HttpStatus.NOT_FOUND, 4001, "해당 가맹점을 찾을 수 없습니다"),
    CANT_FIND_STORE_DEVICE(HttpStatus.NOT_FOUND,4002,"해당 가맹점이 보유한 기기를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}
