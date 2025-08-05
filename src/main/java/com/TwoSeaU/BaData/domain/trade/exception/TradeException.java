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
    NOT_LIKED_POST(HttpStatus.BAD_REQUEST, 3014, "찜한 적 없는 게시글입니다."),
    NOT_FOUND_GIFTICON_CATEGORY(HttpStatus.NOT_FOUND, 3015, "찾을 수 없는 카테고리입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 3016, "결제 정보를 찾을 수 없습니다."),
    ELA_IMAGE_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3017, "ELA 이미지 처리에 실패했습니다."),
    SUSPICIOUS_IMAGE_DETECTED(HttpStatus.BAD_REQUEST, 3018, "조작이 감지되었습니다."),
    REPORT_COMMENT_REQUIRED(HttpStatus.BAD_REQUEST, 3019, "신고 사유가 기타일 경우 상세 사유를 작성해야 합니다."),
    DELETED_POST_ACCESS_DENIED(HttpStatus.GONE, 3020, "삭제된 게시글에 접근할 수 없습니다."),
    ALREADY_LIKED_POST(HttpStatus.CONFLICT, 3021, "이미 찜한 게시글입니다."),
    SOLD_POST_DELETE_DENIED(HttpStatus.FORBIDDEN, 3022, "판매 완료된 게시글은 삭제할 수 없습니다."),
    SELF_PAYMENT_DENIED(HttpStatus.FORBIDDEN, 3023, "본인 게시글은 구매할 수 없습니다."),
    NOT_FOUND_GIFTICON_PARTNER(HttpStatus.NOT_FOUND, 3024, "해당 제휴처를 찾을 수 없습니다."),
    EXPIRED_EXPIRATION_DATE(HttpStatus.BAD_REQUEST, 3025, "유효 기간이 지난 기프티콘입니다."),
    CANNOT_READ_FROM_IMAGE(HttpStatus.BAD_REQUEST, 3026, "이미지 파일을 읽어올 수 없습니다."),
    CANNOT_PARSE_DATE(HttpStatus.BAD_REQUEST, 3027, "날짜 파싱 처리에 실패하였습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, 3028, "결제 금액이 게시글 가격과 일치하지 않습니다."),
    COIN_DECIMAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 3029, "포인트는 소수점 이하를 사용할 수 없습니다."),
    COIN_NOT_ENOUGH(HttpStatus.BAD_REQUEST, 3030, "포인트가 부족합니다."),
    AMOUNT_DECIMAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 3031, "금액은 소수점 이하를 사용할 수 없습니다."),
    COIN_EXCEED_PRICE(HttpStatus.BAD_REQUEST, 3032, "포인트는 게시글 가격을 초과할 수 없습니다."),
    REALTIME_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3033, "실시간 검색 처리에 실패했습니다."),
    REALTIME_SEARCH_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, 3034, "실시간 검색 내용이 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, 3035, "신고 게시글을 찾을 수 없습니다."),
    RECOMMENDATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3036, "추천 게시글 처리에 실패했습니다."),
    DUPLICATE_COUPON_NUMBER(HttpStatus.BAD_REQUEST, 3037, "이미 등록된 기프티콘입니다."),
    NOT_PURCHASED_GIFTICON(HttpStatus.BAD_REQUEST, 3038, "구매하지 않은 기프티콘은 조회할 수 없습니다."),
    GIFTICON_NOT_FOUND(HttpStatus.NOT_FOUND, 3039, "신고하려는 게시글이 기프티콘이 아닙니다."),
    BARCODE_NOT_VIEWED(HttpStatus.BAD_REQUEST, 3040, "바코드를 조회하지 않은 기프티콘은 신고할 수 없습니다."),
    SOLD_POST_ALREADY(HttpStatus.BAD_REQUEST, 3041, "이미 판매 완료된 게시글입니다."),
    REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3042, "환불에 실패했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
