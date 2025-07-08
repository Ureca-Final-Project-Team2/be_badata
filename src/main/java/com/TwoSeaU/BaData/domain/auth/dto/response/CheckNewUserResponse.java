package com.TwoSeaU.BaData.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckNewUserResponse {

    private boolean isNewUser;
    public static CheckNewUserResponse from(final boolean isNewUser){

        return CheckNewUserResponse.builder()
                .isNewUser(isNewUser)
                .build();
    }
}
