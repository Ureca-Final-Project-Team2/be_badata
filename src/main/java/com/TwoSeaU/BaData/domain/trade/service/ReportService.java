package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.request.SavePurchaseReportRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveReportRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveReportResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportType;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final GifticonRepository gifticonRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReportNotificationService reportNotificationService;

    @Transactional
    public SaveReportResponse createReport(final Long postId, final SaveReportRequest saveReportRequest, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findByIdWithLock(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (saveReportRequest.getReportType() == ReportType.ETC){
            if (saveReportRequest.getComment() == null || saveReportRequest.getComment().isEmpty()) {
                throw new GeneralException(TradeException.REPORT_COMMENT_REQUIRED);
            }
        }

        if (reportRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new GeneralException(TradeException.REPORT_ALREADY_SUBMITTED);
        }

        final Report savedReport = reportRepository.save(Report.of(post, user, ReportStatus.QUESTION,
                saveReportRequest.getReportType(), saveReportRequest.getComment()));

        reportNotificationService.sendReportNotification(savedReport);

        return SaveReportResponse.of(savedReport.getId());
    }

    @Transactional
    public SaveReportResponse createPurchaseReport(final Long postId, final SavePurchaseReportRequest savePurchaseReportRequest, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (!post.getIsSold()) {
            throw new GeneralException(TradeException.NOT_PURCHASED_GIFTICON);
        }

        final Gifticon gifticon = gifticonRepository.findById(post.getId())
                .orElseThrow(() -> new GeneralException(TradeException.GIFTICON_NOT_FOUND));

        if (post.getDeadLine().isBefore(LocalDate.now())) {
            throw new GeneralException(TradeException.EXPIRED_EXPIRATION_DATE);
        }

        if (gifticon.getBarcodeViewTime() == null) {
            throw new GeneralException(TradeException.BARCODE_NOT_VIEWED);
        }

        paymentRepository.findByUserIdAndPostIdAndPaymentStatus(user.getId(), post.getId(), PaymentStatus.PAID)
                .orElseThrow(() -> new GeneralException(TradeException.PAYMENT_NOT_FOUND));

        if (reportRepository.existsByUserIdAndPostIdAndReportTypeCode(user.getId(), post.getId(), ReportType.AFTER_TRADE)) {
            throw new GeneralException(TradeException.REPORT_ALREADY_SUBMITTED);
        }

        final Report savedReport = reportRepository.save(Report.of(post, user, ReportStatus.QUESTION,
                ReportType.AFTER_TRADE, savePurchaseReportRequest.getComment()));

        return SaveReportResponse.of(savedReport.getId());
    }
}
