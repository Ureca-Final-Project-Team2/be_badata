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
    private String address_name;
    private String id;
    private String phone;
    private String place_name;
    private String road_address_name;
    private Double x;
    private Double y;

    public static GetAddressResponse from(final Address address){

        return GetAddressResponse.builder()
                .addressId(address.getId())
                .address_name(address.getAddressName())
                .id(address.getKaKaoAddressId())
                .phone(address.getPhone())
                .place_name(address.getPlaceName())
                .road_address_name(address.getRoadAddressName())
                .x(address.getLocation().getX())
                .y(address.getLocation().getY())
                .build();
    }

}
