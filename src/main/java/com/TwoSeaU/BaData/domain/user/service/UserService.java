package com.TwoSeaU.BaData.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllReportResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetReportResponse;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final ReportRepository reportRepository;

	public DataResponse getData(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return DataResponse.of(user.getDataAmount());
	}

	public CoinResponse getCoin(String username) {
		 User user = userRepository.findByUsername(username)
			 .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return CoinResponse.of(user.getCoin());
	}

	public GetAllReportResponse getCompleteReports(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException((UserException.USER_NOT_FOUND)));

		List<GetReportResponse> CompleteReports = reportRepository.findAllByUserIdAndReportStatus(user.getId(), ReportStatus.COMPLETE)
			.stream()
			.map(GetReportResponse::from)
			.toList();

		return GetAllReportResponse.of(CompleteReports);
	}

	public GetAllReportResponse getPendingReports(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		List<GetReportResponse> pendingReports = reportRepository.findAllByUserIdAndReportStatusNot(user.getId(), ReportStatus.COMPLETE)
			.stream()
			.map(GetReportResponse::from)
			.toList();

		return GetAllReportResponse.of(pendingReports);
	}
}
