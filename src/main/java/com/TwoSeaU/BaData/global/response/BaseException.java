package com.TwoSeaU.BaData.global.response;

import org.springframework.http.HttpStatus;

public interface BaseException {
    int getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}
