package com.TwoSeaU.BaData.domain.rental.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReserveDeviceRequest {

    @NotNull(message = "storeDeviceId는 필수입니다.")
    private Long storeDeviceId;

    @NotNull(message = "예약할 수량은 필수입니다.")
    @Min(value = 1, message = "최소 1개 이상 예약해야 합니다.")
    private Integer count;

}
