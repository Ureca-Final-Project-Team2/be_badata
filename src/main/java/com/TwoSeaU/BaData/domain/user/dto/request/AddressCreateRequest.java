package com.TwoSeaU.BaData.domain.user.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressCreateRequest {

    @NotBlank(message = "상세 주소는 빈 값이 될 수 없습니다.")
    private String address_name;

    @NotBlank(message = "카카오 주소 아이디는 빈 값이 될 수 없습니다.")
    private String id;

    @NotBlank(message = "핸드폰 번호는 빈 값이 될 수 없습니다.")
    private String phone;

    @NotBlank(message = "장소 이름은 빈 값이 될 수 없습니다.")
    private String place_name;

    @NotBlank(message = "상세 주소는 빈 값이 될 수 없습니다.")
    private String road_address_name;

    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180 이하이어야 합니다.")
    private double x;

    @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90 이하이어야 합니다.")
    private double y;

}
