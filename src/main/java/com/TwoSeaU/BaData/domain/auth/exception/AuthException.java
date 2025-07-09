package com.TwoSeaU.BaData.domain.auth.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthException implements BaseException {

    // 인증에러 6000번대
    NOT_SUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, 6000, "지원하지 않는 소셜 타입입니다"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED,6001,"접근할 권한이 없습니다(인증예외)"),
    ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN,6002,"접근이 거부되었습니다(권한예외)"),
    REFRESH_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST,6003,"리프레시 토큰이 유효하지 않거나 만료되었습니다");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}

