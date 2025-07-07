package com.TwoSeaU.BaData.domain.trade.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PAID, PENDING, CANCELED;
}
