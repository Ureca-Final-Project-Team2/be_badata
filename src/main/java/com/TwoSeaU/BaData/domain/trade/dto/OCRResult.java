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
	private String partner;
	private String expirationDate;
	private String barcode;

	public static OCRResult of(final String couponName, final String partner, final String expirationDate, final String barcode) {
		return OCRResult.builder()
			.couponName(couponName)
			.partner(partner)
			.expirationDate(expirationDate)
			.barcode(barcode)
			.build();
	}
}
