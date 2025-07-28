package com.TwoSeaU.BaData.domain.user.enums;

import com.TwoSeaU.BaData.domain.auth.exception.AuthException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SocialType {

    KAKAO("kakao");

    private final String value;

    SocialType(String value) {
        this.value = value;
    }

    public static SocialType from(String value) {

        return Arrays.stream(values())
                .filter(s -> s.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()->new GeneralException(AuthException.NOT_SUPPORTED_SOCIAL_TYPE));
    }
}

