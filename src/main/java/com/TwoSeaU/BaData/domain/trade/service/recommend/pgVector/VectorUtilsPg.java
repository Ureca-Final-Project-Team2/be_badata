package com.TwoSeaU.BaData.domain.trade.service.recommend.pgVector;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
public class VectorUtilsPg {

    private static final double MAX_PRICE = 30000;
    private static final double MAX_DAYS_TO_EXPIRY = 365;

    public byte[] vectorFloatToByte(float[] vectorPg) {
        if (vectorPg == null) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(4 * vectorPg.length);
        for (float f : vectorPg) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }

    public float normalizePrice(float price) {
        return (float) Math.min(price / MAX_PRICE, 1.0F);
    }

    //마감기한 정규화
    public float normalizeDaysToExpiry(float days) {
        return (float) Math.min(days / MAX_DAYS_TO_EXPIRY, 1.0);
    }

}