package com.TwoSeaU.BaData.domain.trade.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OCRResult {
	private String couponName;
	private String expirationDate;
	private String barcode;

	public static OCRResult of(final String couponName, final String expirationDate, final String barcode) {
		return OCRResult.builder()
			.couponName(couponName)
			.expirationDate(expirationDate)
			.barcode(barcode)
			.build();
	}
}
