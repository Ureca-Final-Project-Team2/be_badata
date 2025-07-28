package com.TwoSeaU.BaData.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueServiceTokenResponse {

    private String accessToken;
    private String refreshToken;
    private String type;
    private Long accessTokenValidationTime;
    private Long refreshTokenValidationTime;

    public static IssueServiceTokenResponse of(final String accessToken,final String refreshToken,final String type,
                                               final Long accessTokenValidationTime,final Long refreshTokenValidationTime){

        return IssueServiceTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .type(type)
                .accessTokenValidationTime(accessTokenValidationTime)
                .refreshTokenValidationTime(refreshTokenValidationTime)
                .build();
    }
}
