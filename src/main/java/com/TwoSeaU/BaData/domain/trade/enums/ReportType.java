package com.TwoSeaU.BaData.domain.trade.enums;

public enum ReportType {
    FRAUD("거래 사기인 것 같아요"),
    DUPLICATE_POST("무지성 중복 게시글인 것 같아요"),
    RESELLING_HIGH_PRICE("나에게 구매 후 비싸게 재판매 하는 것 같아요"),
    ABOVE_MARKET_PRICE("정가보다 비싸요"),
    FREE_OR_MONEY_REQUEST("무료 나눔 및 금전 요구 글이에요"),
    ETC("기타"),

    AFTER_TRADE("거래 후 신고");


    private final String description;

    ReportType(final String description) {
        this.description = description;
    }
}
