package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportQueryRepository {

	boolean existsByUserIdAndPostId(final Long userId, final Long postId);
	boolean existsByUserIdAndPostIdAndReportTypeCode(final Long userId, final Long postId, final ReportType reportType);
	Long countByUserIdAndReportStatus(final Long userId, final ReportStatus status);
	Long countByPost(final Post post);
}
