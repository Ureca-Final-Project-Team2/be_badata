package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetSocialLoginTokenRequest;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueServiceTokenResponse;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueTokenUserStatusResponse;
import com.TwoSeaU.BaData.domain.auth.exception.AuthException;
import com.TwoSeaU.BaData.domain.auth.jwt.ServiceTokenProvider;
import com.TwoSeaU.BaData.global.redis.RedisUtil;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SocialLoginApiProcessorSelector processorSelector;
    private final SocialUserService socialUserService;
    private final ServiceTokenProvider serviceTokenProvider;
    private final RedisUtil redisUtil;

    public IssueTokenUserStatusResponse getServiceToken(final String code, final String provider) {

        SocialLoginApiProcessor apiProcessor = processorSelector.getApiProcessor(provider);

        GetSocialLoginTokenRequest token = apiProcessor.getSocialLoginTokenIssue(code);
        GetOAuth2UserProfileRequest profile = apiProcessor.getSocialLoginProfile(token.getAccess_token());

        return socialUserService.registerAndGetUser(profile, provider);
    }

    public IssueServiceTokenResponse reIssueServiceToken(final String accessToken, final String refreshToken){

        Authentication authentication = serviceTokenProvider.getAuthentication(accessToken);

        if(redisUtil.getValueByKey(authentication.getName())==null ||
                !redisUtil.getValueByKey(authentication.getName()).equals(refreshToken)){
            throw new GeneralException(AuthException.REFRESH_TOKEN_NOT_VALID);
        }

        IssueServiceTokenResponse issueServiceTokenResponse = serviceTokenProvider.createToken(authentication);

        redisUtil.saveValueByKey(authentication.getName(),issueServiceTokenResponse.getRefreshToken(),issueServiceTokenResponse.getRefreshTokenValidationTime(),TimeUnit.MILLISECONDS);

        return issueServiceTokenResponse;
    }
}
