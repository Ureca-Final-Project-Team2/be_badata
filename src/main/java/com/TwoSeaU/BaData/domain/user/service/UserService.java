package com.TwoSeaU.BaData.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllLikesPostsResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllPurchasesResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllReportResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetPurchaseResponse;
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
	private final PaymentRepository paymentRepository;
	private final PostRepository postRepository;
	private final PostLikesRepository postLikesRepository;

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

	public GetAllPurchasesResponse getAllPurchasesResponse(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		List<Payment> paymentList = paymentRepository.findAllByUserId(user.getId());

		List<GetPurchaseResponse> getPurchaseResponseList = paymentList.stream()
			.map(payment -> {
				Post post = postRepository.findById(payment.getPost().getId())
					.orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));
				int postLikes = postLikesRepository.countByPostId(post.getId());

				return GetPurchaseResponse.from(post, payment, postLikes);
			})
			.toList();

		return GetAllPurchasesResponse.of(getPurchaseResponseList);
	}

	public GetAllLikesPostsResponse getAllLikesPosts(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		List<PostLikes> postLikesList = postLikesRepository.findAllByUserId(user.getId());
		List<GetLikesPostResponse> getLikesPostResponseList = postLikesList.stream()
			.map(postLikes -> {
				Post post = postRepository.findById(postLikes.getPost().getId())
					.orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));
				Long postLikesCount = postLikesRepository.countByPostId(post.getId());

				return GetLikesPostResponse.from(post, postLikesCount);
			})
			.toList();

		return GetAllLikesPostsResponse.of(getLikesPostResponseList);
	}
}
