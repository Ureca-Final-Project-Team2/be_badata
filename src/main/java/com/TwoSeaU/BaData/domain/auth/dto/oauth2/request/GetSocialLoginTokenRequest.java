package com.TwoSeaU.BaData.domain.auth.dto.oauth2.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetSocialLoginTokenRequest {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;

}
