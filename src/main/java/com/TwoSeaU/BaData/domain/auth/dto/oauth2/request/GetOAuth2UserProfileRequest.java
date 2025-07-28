package com.TwoSeaU.BaData.domain.auth.dto.oauth2.request;

public interface GetOAuth2UserProfileRequest {

    String getEmail();

    String getId();

    String getNickName();

    String getProfileImageUrl();

}
