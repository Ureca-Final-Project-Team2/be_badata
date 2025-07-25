package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.OCRResult;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetImageUploadResponse {
    private boolean isManipulated;
    private double ratio;
    private String couponName;
    private String expirationDate;
    private String barcode;

    public static GetImageUploadResponse from(final ELAResult elaResult, final OCRResult ocrResult) {
        return GetImageUploadResponse.builder()
                .isManipulated(elaResult.isManipulated())
                .ratio(elaResult.getRatio())
                .couponName(ocrResult.getCouponName())
                .expirationDate(ocrResult.getExpirationDate())
                .barcode(ocrResult.getBarcode())
                .build();
    }
}
