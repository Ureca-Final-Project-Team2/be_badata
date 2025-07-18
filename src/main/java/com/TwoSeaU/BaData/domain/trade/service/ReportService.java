package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveReportRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveReportResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportType;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public SaveReportResponse createReport(final Long postId, final SaveReportRequest saveReportRequest, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if(saveReportRequest.getReportType() == ReportType.ETC){
            if (saveReportRequest.getComment() == null || saveReportRequest.getComment().isEmpty()) {
                throw new GeneralException(TradeException.REPORT_COMMENT_REQUIRED);
            }
        }

        if(reportRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new GeneralException(TradeException.REPORT_ALREADY_SUBMITTED);
        }

        final Report savedReport = reportRepository.save(Report.of(post, user, ReportStatus.QUESTION,
                saveReportRequest.getReportType(), saveReportRequest.getComment()));

        return SaveReportResponse.of(savedReport.getId());
    }
}
