package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetSocialLoginTokenRequest;

public interface SocialLoginApiProcessor {
    GetSocialLoginTokenRequest getSocialLoginTokenIssue(final String code);

    GetOAuth2UserProfileRequest getSocialLoginProfile(final String accessToken);
}
