package com.TwoSeaU.BaData.global.fcm;

import com.TwoSeaU.BaData.global.exception.GlobalException;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    public void send(final NotificationRequest notificationRequest) {
        try {
            Message.Builder builder = Message.builder()
                    .putData("title", notificationRequest.getTitle())
                    .putData("content", notificationRequest.getContent())
                    .setToken(notificationRequest.getFcmTokens().get(0)); // 단건 전송용 (첫 번째 토큰)

            // 추가 데이터 처리
            if (notificationRequest.getData() != null) {
                for (Map.Entry<String, String> entry : notificationRequest.getData().entrySet()) {
                    builder.putData(entry.getKey(), entry.getValue());
                }
            }

            FirebaseMessaging.getInstance().send(builder.build());

        } catch (FirebaseMessagingException e) {
            MessagingErrorCode messagingErrorCode = e.getMessagingErrorCode();

            // 로그 출력 및 만료 토큰 처리
            if (messagingErrorCode == MessagingErrorCode.UNREGISTERED || messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT) {

                final String expiredToken = notificationRequest.getFcmTokens().get(0);

                if(messagingErrorCode == MessagingErrorCode.UNREGISTERED){
                    log.warn("만료된 fcmToken: {}", expiredToken);
                    throw new GeneralException(GlobalException.FIREBASE_TOKEN_EXPIRED);
                }
                if(messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT){
                    log.warn("유효하지 않은 fcmToken: {}", expiredToken);
                    throw new GeneralException(GlobalException.FIREBASE_TOKEN_NOT_VALID);
                }

            } else {
                log.error("Failed to send FCM: {}", messagingErrorCode, e);
                throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
            }
        }
    }

    public void sendToManyUser(final NotificationRequest notificationRequest) {

        final List<String> tokens = notificationRequest.getFcmTokens();

        if (tokens == null || tokens.isEmpty()) return;

        try {

            MulticastMessage.Builder builder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .putData("title", notificationRequest.getTitle())
                    .putData("content", notificationRequest.getContent());

            if (notificationRequest.getData() != null) {
                for (Map.Entry<String, String> entry : notificationRequest.getData().entrySet()) {
                    builder.putData(entry.getKey(), entry.getValue());
                }
            }

            final MulticastMessage message = builder.build();

            final BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {

                    final SendResponse sendResponse = responses.get(i);

                    if (!sendResponse.isSuccessful()) {

                        final String failedToken = tokens.get(i);

                        FirebaseMessagingException ex = sendResponse.getException();
                        MessagingErrorCode messagingErrorCode = ex.getMessagingErrorCode();

                        if (messagingErrorCode == MessagingErrorCode.UNREGISTERED || messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT) {

                            final String expiredToken = failedToken;

                            if(messagingErrorCode == MessagingErrorCode.UNREGISTERED){
                                log.warn("만료된 fcmToken: {}", expiredToken);
                                throw new GeneralException(GlobalException.FIREBASE_TOKEN_EXPIRED);
                            }
                            if(messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT){
                                log.warn("유효하지 않은 fcmToken: {}", expiredToken);
                                throw new GeneralException(GlobalException.FIREBASE_TOKEN_NOT_VALID);
                            }

                        } else {
                            log.error("Failed to send FCM: {}", failedToken, messagingErrorCode);
                            throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
                        }
                    }
                }
            }

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast message", e);
            throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
        }
    }
}
