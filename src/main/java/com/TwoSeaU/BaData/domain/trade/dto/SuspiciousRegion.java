package com.TwoSeaU.BaData.domain.trade.dto;

import lombok.*;

@Builder
public class SuspiciousRegion {
    public final int x, y, width, height;
    public final double score;

    public static SuspiciousRegion of(int x, int y, int width, int height, double score) {
        return SuspiciousRegion.builder()
                .x(x)
                .y(y)
                .width(width)
                .height(height)
                .score(score)
                .build();
    }
}
