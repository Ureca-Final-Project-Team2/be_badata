package com.TwoSeaU.BaData.domain.sos.exception;

import org.springframework.http.HttpStatus;

import com.TwoSeaU.BaData.global.response.BaseException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SosException implements BaseException {
	//SOS 관련 에러 코드 - 5000 번대
	SOS_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "해당 SOS을 찾을 수 없습니다"),
	ALREADY_RESPONDER_EXIST(HttpStatus.BAD_REQUEST, 5002, "이미 응답자가 존재합니다."),
	CANNOT_RESPOND_TO_OWN_SOS(HttpStatus.FORBIDDEN, 5003, "자신이 요청한 SOS에는 응답할 수 없습니다."),
	CANNOT_CONNECT_SSE(HttpStatus.BAD_REQUEST, 5004, "SSE 연결에 실패하였습니다.")
	;

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
