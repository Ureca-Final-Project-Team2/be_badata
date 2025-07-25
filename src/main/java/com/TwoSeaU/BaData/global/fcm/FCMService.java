package com.TwoSeaU.BaData.global.fcm;

import com.TwoSeaU.BaData.global.exception.GlobalException;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMService {

    public void send(final NotificationRequest notificationRequest){

        try{
            final Message message = Message.builder()
                    .putData("title", notificationRequest.getTitle())
                    .putData("content", notificationRequest.getContent())
                    .setToken(notificationRequest.getFcmToken())
                    .build();

            FirebaseMessaging.getInstance().send(message);

        } catch (FirebaseMessagingException e){

            throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
        }

    }

}
