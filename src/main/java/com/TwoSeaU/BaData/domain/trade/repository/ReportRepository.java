package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportQueryRepository {

	boolean existsByUserIdAndPostId(Long userId, Long postId);
	Long countByUserIdAndReportStatus(final Long userId, final ReportStatus status);
}
