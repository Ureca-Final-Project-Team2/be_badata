package com.TwoSeaU.BaData.domain.user.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.user.dto.response.CreateFollowResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetCoinHistoryResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetReportInfoResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetTotalPostCountResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetTotalReportCountResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetUserInfoResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.UpdateNotificationSettingResponse;
import com.TwoSeaU.BaData.domain.user.entity.PlanData;
import com.TwoSeaU.BaData.domain.user.entity.UserLikes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TwoSeaU.BaData.domain.rental.repository.ReStockRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreLikesRepository;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetDataResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetFollowsResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesStoreResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetPurchaseResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRentalResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetReportResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRestockResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSosResponse;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.FollowType;
import com.TwoSeaU.BaData.domain.user.enums.TradeType;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.CoinHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.PlanDataRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserLikesRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PlanDataRepository planDataRepository;
	private final ReportRepository reportRepository;
	private final PaymentRepository paymentRepository;
	private final PostRepository postRepository;
	private final PostLikesRepository postLikesRepository;
	private final SosRepository sosRepository;
	private final UserLikesRepository userLikesRepository;
	private final StoreLikesRepository storeLikesRepository;
	private final ReservationRepository reservationRepository;
	private final ReStockRepository reStockRepository;
	private final CoinHistoryRepository coinHistoryRepository;

	public GetDataResponse getData(String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final PlanData planData = planDataRepository.findById(user.getPlanData().getId())
			.orElseThrow(() -> new GeneralException(UserException.PLAN_NOT_FOUND));

		return GetDataResponse.from(user, planData);
	}

	public CoinResponse getCoin(String username) {
		 User user = userRepository.findByUsername(username)
			 .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return CoinResponse.of(user.getCoin());
	}

	public GetTotalPostCountResponse getTotalPostCount(final TradeType tradeType, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final int totalCount = tradeType == TradeType.SALE
			? postRepository.countBySellerId(user.getId()) : paymentRepository.countByUserId(user.getId());

		return GetTotalPostCountResponse.of(totalCount);
	}


	public GetUserInfoResponse getUserInfo(final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final LocalDate now = LocalDate.now();
		final LocalDate createdDate = user.getCreatedAt().toLocalDate();

		final Long diffDays = ChronoUnit.DAYS.between(createdDate, now)+1;
		return GetUserInfoResponse.from(user, diffDays);
	}

	public CursorPageResponse<GetCoinHistoryResponse> getAllCoinsByCursor(final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return coinHistoryRepository.getAllCoinsResponse(cursor, size, user.getId());
	}

	public CursorPageResponse<GetReportResponse> getAllReportsByCursor(final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException((UserException.USER_NOT_FOUND)));

		return reportRepository.getAllReportsByCursor(cursor, size, user.getId());
	}

	public GetReportInfoResponse getReportInfo(final Long reportId, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new GeneralException(TradeException.REPORT_NOT_FOUND));

		final Payment payment = paymentRepository.findByUserIdAndPostIdAndPaymentStatus(user.getId(), report.getPost().getId(), PaymentStatus.PAID)
			.orElseThrow(() -> new GeneralException(TradeException.PAYMENT_NOT_FOUND));

		return GetReportInfoResponse.from(report, payment.getCreatedAt());
	}

	public GetTotalReportCountResponse getTotalReportCount(final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Long questionCount = reportRepository.countByUserIdAndReportStatus(user.getId(), ReportStatus.QUESTION);
		final Long answerCount = reportRepository.countByUserIdAndReportStatus(user.getId(), ReportStatus.ANSWER);
		final Long completeCount = reportRepository.countByUserIdAndReportStatus(user.getId(), ReportStatus.COMPLETE);

		return GetTotalReportCountResponse.of(questionCount, answerCount, completeCount);
	}

	public CursorPageResponse<GetPurchaseResponse> getAllPurchasesByCursor(final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return paymentRepository.getAllPurchasesByCursor(cursor, size, user.getId());
	}

	public CursorPageResponse<GetLikesPostResponse> getAllLikesPostsByCursor(final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return postLikesRepository.getAllLikesPostsByCursor(cursor, size, user.getId());
	}

	public CursorPageResponse<GetSaleResponse> getAllSalesByCursor(PostCategory postCategory, Boolean isSold, Long cursor, int size, String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return postRepository.getAllSalesByCursor(postCategory, isSold, cursor, size, user.getId());
	}

	public CursorPageResponse<GetSosResponse> getAllSosByCursor(Long cursor, int size, String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return sosRepository.getAllSosResponse(cursor, size, user.getId());
	}

	@Transactional
	public CreateFollowResponse createFollow(final Long userId, final String username){

		final User loginUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final User followingUser = userRepository.findById(userId)
				.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		if (loginUser.getUsername().equals(followingUser.getUsername())){
			throw new GeneralException(UserException.CANT_FOLLOW_SELF);
		}

		return userLikesRepository.findByFollowerUserAndFollowingUser(loginUser, followingUser)
				.map(userLikes -> {
					userLikesRepository.delete(userLikes);
					return CreateFollowResponse.of(false);
				})
				.orElseGet(() -> {
					userLikesRepository.save(UserLikes.of(followingUser, loginUser));
					return CreateFollowResponse.of(true);
				});
	}

	@Transactional
	public Long deleteFollow(final Long followId, final String username){

		final User loginUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final UserLikes userLikes = userLikesRepository.findById(followId).orElseThrow(
				()-> new GeneralException(UserException.LIKES_USER_NOT_FOUND));

		if(!userLikes.getFollowerUser().getUsername().equals(loginUser.getUsername()) &&
		    !userLikes.getFollowingUser().getUsername().equals(loginUser.getUsername())){

			throw new GeneralException(UserException.CANT_DELETE_OTHER_FOLLOW);
		}

		userLikesRepository.delete(userLikes);

		return followId;
	}

	public CursorPageResponse<GetFollowsResponse> getFollowsByCursor(final FollowType followType, final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		if(followType == FollowType.FOLLOWERS) {
			return userLikesRepository.getAllFollowersResponse(cursor, size, user.getId());
		}
		else {
			return userLikesRepository.getAllFollowingsResponse(cursor, size, user.getId());
		}
	}

	public CursorPageResponse<GetLikesStoreResponse> getAllLikesStoresByCursor(final Long cursor, final int size, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return storeLikesRepository.getAllLikesStoresResponseByCursor(cursor, size, user.getId());
	}

	public CursorPageResponse<GetRentalResponse> getAllRentalsByCursor(final Long cursor, final int size, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return reservationRepository.getAllRentalsByCursor(cursor, size, user.getId());
	}

	public CursorPageResponse<GetRestockResponse> getAllRestocksByCursor(final Long cursor, final int size, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return reStockRepository.getAllRestocksByCursor(cursor, size, user.getId());
	}

	@Transactional
	public UpdateNotificationSettingResponse updateNotificationSetting(final Boolean isEnabled, final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		user.updateNotificationSetting(isEnabled);
		return UpdateNotificationSettingResponse.from(user);
	}
}
