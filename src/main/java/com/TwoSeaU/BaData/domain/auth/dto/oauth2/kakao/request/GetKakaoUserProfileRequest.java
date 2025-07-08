package com.TwoSeaU.BaData.domain.auth.dto.oauth2.kakao.request;

import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetKakaoUserProfileRequest implements GetOAuth2UserProfileRequest {

    private String id;

    @JsonProperty("connected_at")
    private String connectedAt;

    private KakaoProperties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getNickName() {
        return kakaoAccount.getProfile().getNickname();
    }
    @Override
    public String getProfileImageUrl() {
        return kakaoAccount.getProfile().getProfileImageUrl();
    }
}
