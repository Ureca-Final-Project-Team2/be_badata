package com.TwoSeaU.BaData.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueTokenUserStatusResponse {

    private IssueServiceTokenResponse issueServiceTokenResponse;
    private LoginUserResponse loginUserResponse;

    public static IssueTokenUserStatusResponse of(final IssueServiceTokenResponse issueServiceTokenResponse, final LoginUserResponse loginUserResponse){

        return new IssueTokenUserStatusResponse(issueServiceTokenResponse,loginUserResponse);
    }
}
