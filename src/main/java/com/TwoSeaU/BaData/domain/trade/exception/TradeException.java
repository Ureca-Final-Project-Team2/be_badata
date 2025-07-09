package com.TwoSeaU.BaData.domain.trade.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TradeException implements BaseException {
    //거래 커뮤니티 에러 - 3000번 대
    SEARCH_NO_RESULT(HttpStatus.NOT_FOUND, 3000, "검색 결과가 존재하지 않습니다."),
    TRENDING_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3001, "실시간 검색 처리에 실패했습니다."),
    REPORT_INVALID_TARGET(HttpStatus.BAD_REQUEST, 3002, "잘못된 신고 대상입니다."),
    REPORT_ALREADY_SUBMITTED(HttpStatus.CONFLICT, 3003, "이미 신고한 게시글입니다."),
    LIKES_UNAUTHORIZED(HttpStatus.FORBIDDEN, 3004, "본인 게시글은 찜할 수 없습니다."),
    PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3005, "결제에 실패했습니다."),
    PAYMENT_DUPLICATE(HttpStatus.CONFLICT, 3006, "이미 거래된 게시글입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 3007, "해당 게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, 3008, "게시글에 접근할 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 3009, "찾을 수 없는 유저입니다."),
    RECENT_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, 3010, "최근 검색 기록을 찾을 수 없습니다."),
    OCR_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3011, "OCR 처리에 실패했습니다."),
    EXPIRED_POST_ACCESS(HttpStatus.BAD_REQUEST, 3012, "이미 마감 기한이 지난 게시글입니다."),
    EXPIRED_POST_MODIFY(HttpStatus.BAD_REQUEST, 3013, "마감 기한이 지난 게시글은 수정할 수 없습니다."),
    NOT_LIKED_POST(HttpStatus.BAD_REQUEST, 3014, "찜한 적 없는 게시글입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
