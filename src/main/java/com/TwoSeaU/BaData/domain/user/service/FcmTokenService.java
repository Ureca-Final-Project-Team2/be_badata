package com.TwoSeaU.BaData.domain.user.service;

import com.TwoSeaU.BaData.domain.user.dto.request.FcmTokenRequest;
import com.TwoSeaU.BaData.domain.user.entity.FcmToken;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.FcmTokenRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    public Long saveFcmToken(final String username, final FcmTokenRequest fcmTokenRequest){

        final User user = userRepository.findByUsername(username).orElseThrow(() -> new GeneralException(
                UserException.USER_NOT_FOUND));

        final FcmToken fcmToken = FcmToken.of(user, fcmTokenRequest.getFcmToken());

        return fcmTokenRepository.save(fcmToken).getId();
    }

}
