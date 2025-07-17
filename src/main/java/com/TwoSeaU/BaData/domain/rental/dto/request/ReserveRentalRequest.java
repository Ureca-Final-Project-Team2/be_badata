package com.TwoSeaU.BaData.domain.rental.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReserveRentalRequest {

    @Valid
    @NotEmpty(message = "예약할 기기 목록은 비어 있을 수 없습니다.")
    private List<ReserveDeviceRequest> storeDevices;

    @NotNull(message = "대여 시작일은 필수입니다.")
    private LocalDateTime rentalStartDate;

    @NotNull(message = "대여 종료일은 필수입니다.")
    private LocalDateTime rentalEndDate;

    @NotNull(message = "가맹점 아이디를 입력해 주세요")
    private Long storeId;

}
