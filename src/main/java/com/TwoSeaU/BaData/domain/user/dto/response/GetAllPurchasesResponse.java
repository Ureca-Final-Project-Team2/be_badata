package com.TwoSeaU.BaData.domain.user.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAllPurchasesResponse {

	private List<GetPurchaseResponse> purchaseResponseList;

	public static GetAllPurchasesResponse of(List<GetPurchaseResponse> purchaseResponseList) {
		return GetAllPurchasesResponse.builder()
			.purchaseResponseList(purchaseResponseList)
			.build();
	}
}
