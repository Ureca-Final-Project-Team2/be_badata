package com.TwoSeaU.BaData.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final int code;

    private final String message;

    private T content;

    private static final int SUCCESS_CODE = 20000;

    private static final int INPUT_ERROR = 500;

    public ApiResponse(T data) {

        this.code = SUCCESS_CODE; // 임시 성공 코드
        this.message = null;
        this.content = data;
    }

    public ApiResponse(BaseException baseException) {

        this.code = baseException.getCode();
        this.message = baseException.getMessage();
        this.content = null;
    }

    public ApiResponse(String errorMessage){

        this.code = INPUT_ERROR;
        this.message = errorMessage;
        this.content = null;
    }

    public static <T> ApiResponse<T> success(T data) {

        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> error(GeneralException generalException) {

        return new ApiResponse<>(generalException.getBaseException());
    }

    public static <T> ApiResponse<T> inputError(String inputMessageError){

        return new ApiResponse<>(inputMessageError);
    }
}

