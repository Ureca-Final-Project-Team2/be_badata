package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetSocialLoginTokenRequest;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueTokenUserStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SocialLoginApiProcessorSelector processorSelector;
    private final SocialUserService socialUserService;

    public IssueTokenUserStatusResponse getServiceToken(String code, String provider) {

        SocialLoginApiProcessor apiProcessor = processorSelector.getApiProcessor(provider);

        GetSocialLoginTokenRequest token = apiProcessor.getSocialLoginTokenIssue(code);
        GetOAuth2UserProfileRequest profile = apiProcessor.getSocialLoginProfile(token.getAccess_token());

        return socialUserService.registerAndGetUser(profile, provider);
    }
}
