package com.TwoSeaU.BaData.domain.rental.event;

import com.TwoSeaU.BaData.domain.rental.service.RestockDeleteTargetingService;
import com.TwoSeaU.BaData.domain.user.entity.FcmToken;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.repository.FcmTokenRepository;
import com.TwoSeaU.BaData.global.email.MailService;
import com.TwoSeaU.BaData.global.fcm.FCMService;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalListenerEvent {

    private final MailService mailService;
    private final FCMService fcmService;
    private final RestockDeleteTargetingService restockDeleteTargetingService;
    private final FcmTokenRepository fcmTokenRepository;
    private static final String restockTitle = "찜한 와이파이 왔어요!";
    private static final String restockContent = " 지금 아니면 또 놓칠지도 몰라요, 파도처럼 \uD83C\uDF0A";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("threadPoolTaskExecutor")
    public void restockEventListener(final RestockEvent restockEvent){

        final List<User> sendTargetUser = restockDeleteTargetingService.getSendTargetUser(restockEvent.getReservation(), restockEvent.getDeviceReservations());

        sendFcmToken(sendTargetUser);
        sendEmail(sendTargetUser);
    }

    private void sendEmail(final List<User> sendTargetUsers){

        final List<String> emails = sendTargetUsers.stream().map(User::getEmail).toList();

        for (final String email: emails){
            mailService.sendMail(email, restockTitle, restockContent);
        }
    }

    private void sendFcmToken(final List<User> sendTargetUsers){

        final List<String> fcmTokens = fcmTokenRepository.findByUserIn(sendTargetUsers)
                .stream().map(FcmToken::getToken).toList();

        fcmService.sendToManyUser(NotificationRequest.forMultipleTokens(restockTitle, restockContent, fcmTokens, Map.of()));
    }
}
