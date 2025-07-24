package com.TwoSeaU.BaData.domain.user.service;

import com.TwoSeaU.BaData.domain.user.dto.response.CreateFollowResponse;
import com.TwoSeaU.BaData.domain.user.entity.UserLikes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TwoSeaU.BaData.domain.rental.repository.ReStockRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreLikesRepository;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;
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
import com.TwoSeaU.BaData.domain.user.exception.UserException;
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
	private final ReportRepository reportRepository;
	private final PaymentRepository paymentRepository;
	private final PostRepository postRepository;
	private final PostLikesRepository postLikesRepository;
	private final SosRepository sosRepository;
	private final UserLikesRepository userLikesRepository;
	private final StoreLikesRepository storeLikesRepository;
	private final ReservationRepository reservationRepository;
	private final ReStockRepository reStockRepository;

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

	public CursorPageResponse<GetReportResponse> getAllReportsByCursor(final ReportStatus reportStatus, final Long cursor, final int size, final String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException((UserException.USER_NOT_FOUND)));

		return reportRepository.getAllReportsByCursor(reportStatus, cursor, size, user.getId());
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
}
