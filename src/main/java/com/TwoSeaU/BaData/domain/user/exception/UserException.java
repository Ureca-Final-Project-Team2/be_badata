package com.TwoSeaU.BaData.domain.user.exception;

import com.TwoSeaU.BaData.global.response.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserException implements BaseException {

    // 사용자 에러 2000번 때
    OAUTH_AUTHORIZATION_FAILED(HttpStatus.BAD_REQUEST, 2000, "로그인에 실패하셨습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "데이터 사용량 정보를 찾을 수 없습니다."),
    RENTAL_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "렌탈 내역이 존재하지 않습니다."),
    LIKES_STORES_NOT_FOUND(HttpStatus.NOT_FOUND, 2003, "찜한 매장이 없습니다."),
    RESTOCK_ALERT_NOT_FOUND(HttpStatus.NOT_FOUND, 2004, "재입고 알림이 존재하지 않습니다."),
    SOS_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 2005, "SOS 요청 내역이 존재하지 않습니다."),
    SALES_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 2006, "판매 내역이 존재하지 않습니다."),
    PURCHASE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 2007, "구매 내역이 존재하지 않습니다."),
    COIN_NOT_FOUND(HttpStatus.NOT_FOUND, 2008, "보유한 코인 정보가 존재하지 않습니다."),
    FOLLOWING_NOT_FOUND(HttpStatus.NOT_FOUND, 2009, "팔로잉한 게시글이 없습니다."),
    LIKES_POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, 2010, "찜한 거래 물품이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
