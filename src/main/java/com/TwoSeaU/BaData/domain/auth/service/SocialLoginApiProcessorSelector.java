package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.exception.AuthException;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginApiProcessorSelector {

    private final KakaoLoginApiProcessor kakaoLoginApiProcessor;

    public SocialLoginApiProcessor getApiProcessor(final String provider){

        SocialType socialType = SocialType.from(provider);

        if(socialType == SocialType.KAKAO){

            return kakaoLoginApiProcessor;
        }

        throw new GeneralException(AuthException.NOT_SUPPORTED_SOCIAL_TYPE);
    }

}
