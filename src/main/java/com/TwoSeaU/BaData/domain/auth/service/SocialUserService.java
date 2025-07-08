package com.TwoSeaU.BaData.domain.auth.service;

import com.TwoSeaU.BaData.domain.auth.jwt.ServiceTokenProvider;
import com.TwoSeaU.BaData.domain.auth.dto.oauth2.request.GetOAuth2UserProfileRequest;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueTokenUserStatusResponse;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueServiceTokenResponse;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.redis.RedisUtil;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialUserService {

    private final ServiceTokenProvider serviceTokenProvider;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public IssueTokenUserStatusResponse registerAndGetUser(final GetOAuth2UserProfileRequest getOAuth2UserProfileRequest,
                                                            final String provider){

        boolean isNewUser = false;
        SocialType socialType = SocialType.from(provider);
        Optional<User> optionalUser = userRepository.findByUsername(socialType+getOAuth2UserProfileRequest.getId());
        User user;

        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        else{
            isNewUser = true;
            user = userRepository.save(User.of(
                    getOAuth2UserProfileRequest.getNickName(),
                    socialType+getOAuth2UserProfileRequest.getId(),
                    null,
                    Role.GENERAL,
                    socialType,
                    getOAuth2UserProfileRequest.getEmail(),
                    getOAuth2UserProfileRequest.getProfileImageUrl()
            ));
        }

        IssueServiceTokenResponse issueServiceTokenResponse = serviceTokenProvider.createTokenByUserProperty(user.getUsername(),user.getRole().name());
        saveRefreshTokenAtRedis(user.getUsername(), issueServiceTokenResponse);

        return IssueTokenUserStatusResponse.of(issueServiceTokenResponse,isNewUser);
    }

    private void saveRefreshTokenAtRedis(String key, IssueServiceTokenResponse issueServiceTokenResponse){
        redisUtil.saveValueByKey(key, issueServiceTokenResponse.getRefreshToken(),
                issueServiceTokenResponse.getRefreshTokenValidationTime(),TimeUnit.MILLISECONDS);
    }

}

