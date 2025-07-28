package com.TwoSeaU.BaData.global.exception.handler;

import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse> handleGeneralException(GeneralException e) {

        return ResponseEntity.status(e.getBaseException().getHttpStatus())
                .body(ApiResponse.error(e));
    }

    /**
     * DTO Validation 관련 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleControllerValidationException(
            MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity.badRequest()
                .body(ApiResponse.inputError(errorMessage));
    }

}
