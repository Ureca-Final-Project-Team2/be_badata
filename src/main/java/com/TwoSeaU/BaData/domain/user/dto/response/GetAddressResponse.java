package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.user.entity.Address;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAddressResponse {

    private Long addressId;
    private String detailAddress;
    private double longtitude;
    private double latitude;

    public static GetAddressResponse from(final Address address){

        return GetAddressResponse.builder()
                .addressId(address.getId())
                .detailAddress(address.getDetailAddress())
                .longtitude(address.getLocation().getX())
                .latitude(address.getLocation().getY())
                .build();
    }

}
