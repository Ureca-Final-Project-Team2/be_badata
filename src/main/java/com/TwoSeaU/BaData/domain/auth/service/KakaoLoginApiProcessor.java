package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.kakao.request.GetKakaoUserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetSocialLoginTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginApiProcessor implements SocialLoginApiProcessor {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    private static String baseKaKaoTokenUrl="https://kauth.kakao.com/oauth/token";

    private static String baseKaKaoProfileRequestUrl = "https://kapi.kakao.com/v2/user/me";
    @Override
    public GetSocialLoginTokenRequest getSocialLoginTokenIssue(final String code) {

        GetSocialLoginTokenRequest getSocialLoginTokenRequest = webClient.get()
                .uri(baseKaKaoTokenUrl+"?grant_type=authorization_code&client_id="+kakaoClientId+"&client_secret="+kakaoClientSecret+"&code="+code)
                .retrieve()
                .bodyToMono(GetSocialLoginTokenRequest.class)
                .block();

        return getSocialLoginTokenRequest;
    }

    @Override
    public GetOAuth2UserProfileRequest getSocialLoginProfile(final String accessToken) {

        GetKakaoUserProfileRequest KakaoUseProfileInfo =  webClient.get()
                .uri(baseKaKaoProfileRequestUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GetKakaoUserProfileRequest.class)
                .block();

        return KakaoUseProfileInfo;
    }
}
