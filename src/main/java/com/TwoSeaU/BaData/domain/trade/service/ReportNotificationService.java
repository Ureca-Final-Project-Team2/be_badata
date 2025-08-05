package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.event.ReportEmailEvent;
import com.TwoSeaU.BaData.domain.trade.event.ReportFcmEvent;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.entity.FcmToken;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.repository.FcmTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportNotificationService {

    private final ReportRepository reportRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FcmTokenRepository fcmTokenRepository;
    private static final String REPORTED_TITLE = "당신의 게시물은 신고 당하였습니다";
    private static final String REPORTED_CONTENTS_PREFIX="당신의 게시물(제목:";
    private static final String REPORTED_CONTENTS_POSTFIX=") 이 3회 이상 신고 접수 되었습니다. 문제가 있다면 고객 센터로 문의 주세요";
    private static final String REPORT_TITLE = "신고해 주셔서 감사합니다";
    private static final String REPORT_CONTENT_PREFIX = "제목: ";
    private static final String REPORT_CONTENT_POSTFIX = " 게시물에 대해 신고접수가 완료되었습니다. 추가 문의 사항은 고객센터로 문의 주세요";

    private static long REPORT_THRESHOLD = 3;

    @Transactional(readOnly = true)
    @Async("threadPoolTaskExecutor")
    public void sendReportNotification(final Report report){

        final long reportCount = reportRepository.countByPost(report.getPost());
        final Post post = report.getPost();
        final User reporter = report.getUser();
        final User reportedUser = post.getSeller();
        final List<String> reporterFcmTokens = fcmTokenRepository.findByUser(reporter).stream().map(
                FcmToken::getToken).toList();
        final List<String> reportedUserFcmTokens = fcmTokenRepository.findByUser(reportedUser).stream().map(
                FcmToken::getToken).toList();

        if (reportCount == REPORT_THRESHOLD){
            applicationEventPublisher.publishEvent(
                    ReportEmailEvent.of(REPORTED_TITLE,REPORTED_CONTENTS_PREFIX+post.getTitle()+REPORTED_CONTENTS_POSTFIX,reportedUser.getEmail()));
            applicationEventPublisher.publishEvent(ReportFcmEvent.of(
                    REPORTED_TITLE, REPORTED_CONTENTS_PREFIX+post.getTitle()+REPORTED_CONTENTS_POSTFIX,reportedUserFcmTokens)
            );
        }

        applicationEventPublisher.publishEvent(
                ReportEmailEvent.of(REPORT_TITLE,REPORT_CONTENT_PREFIX+post.getTitle()+REPORT_CONTENT_POSTFIX,reporter.getEmail()));
        applicationEventPublisher.publishEvent(ReportFcmEvent.of(
                REPORT_TITLE, REPORT_CONTENT_PREFIX+post.getTitle()+REPORT_CONTENT_POSTFIX,reporterFcmTokens)
        );
    }

}
