package com.TwoSeaU.BaData.global.fcm;

import com.TwoSeaU.BaData.global.exception.GlobalException;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
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
            throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
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
                    if (!responses.get(i).isSuccessful()) {
                        String failedToken = tokens.get(i);
                        log.info("전송 실패 토큰: {}",failedToken);
                    }
                }
            }

        } catch (FirebaseMessagingException e) {
            e.printStackTrace(); // 전체 예외 메시지 확인
            throw new GeneralException(GlobalException.INTERNAL_FIREBASE_ERROR);
        }
    }
}
