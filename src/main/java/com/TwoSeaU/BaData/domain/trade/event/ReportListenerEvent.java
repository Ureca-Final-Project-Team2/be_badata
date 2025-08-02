package com.TwoSeaU.BaData.domain.trade.event;

import com.TwoSeaU.BaData.global.email.MailService;
import com.TwoSeaU.BaData.global.fcm.FCMService;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReportListenerEvent {

    private final MailService mailService;
    private final FCMService fcmService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void creatReportEmailEventListener(final ReportEmailEvent reportEmailEvent){

        mailService.sendMail(reportEmailEvent.getTargetEmail(), reportEmailEvent.getTitle(), reportEmailEvent.getContents());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void creatReportFcmEventListener(final ReportFcmEvent reportFcmEvent){

        fcmService.sendToManyUser(NotificationRequest.forMultipleTokens(reportFcmEvent.getTitle(),
                                                                        reportFcmEvent.getContents(),
                                                                        reportFcmEvent.getFcmTokens(),
                                                                        Map.of()));
    }

}
