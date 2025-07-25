package com.TwoSeaU.BaData.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmTokenRequest {

    @NotBlank(message = "토큰의 값은 비어있을 수 없습니다.")
    private String fcmToken;

}
