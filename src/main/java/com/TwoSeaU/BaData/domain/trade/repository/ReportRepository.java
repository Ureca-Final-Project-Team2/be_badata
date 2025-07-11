package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
	List<Report> findAllByUserIdAndReportStatus(Long userId, ReportStatus reportStatus);
	List<Report> findAllByUserIdAndReportStatusNot(Long userId, ReportStatus reportStatus);
}
