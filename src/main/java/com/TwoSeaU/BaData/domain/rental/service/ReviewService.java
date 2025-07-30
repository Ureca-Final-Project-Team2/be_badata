package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.UpdateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowCountPerQuickReplyResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewMetaResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewOneResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewWithMetaResponse;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.QuickReply;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.TwoSeaU.BaData.domain.rental.entity.ReviewQuickReply;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.QuickReplyRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReviewQuickReplyRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReviewRepository;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.CoinSource;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.CoinHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReviewQuickReplyRepository reviewQuickReplyRepository;
    private final QuickReplyRepository quickReplyRepository;
    private final StoreRepository storeRepository;
    private final CoinHistoryRepository coinHistoryRepository;
    private final DeviceReservationRepository deviceReservationRepository;

    @Transactional
    public Long createReview(final CreateReviewRequest createReviewRequest, final String username, final String imageUrl){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.USER_NOT_FOUND));

        final Reservation reservation = reservationRepository.findById(createReviewRequest.getReservationId())
                        .orElseThrow(()->new GeneralException(RentalException.RESERVATION_NOT_FOUND));

        checkWriteReview(loginUser, reservation);

        final Review review = reviewRepository.save(Review.of(reservation, createReviewRequest.getComment(),createReviewRequest.getRating(), imageUrl));

        saveQuickReply(createReviewRequest.getQuickReplyIds(), review);

        reservation.getStore().adjustReviewRatingByAdd(review);

        final Integer rewardCoin = reservation.getPrice()/10;
        loginUser.addCoin(rewardCoin);

        coinHistoryRepository.save(CoinHistory.of(
            loginUser,
            CoinSource.REVIEW_REWARD,
            rewardCoin,
            loginUser.getCoin()
        ));

        return review.getId();
    }

    @Transactional
    public Long deleteReview(final Long reviewId, final String username){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(UserException.USER_NOT_FOUND));
        final Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new GeneralException(RentalException.REVIEW_NOT_FOUND));

        checkReviewOwner(loginUser, review);
        checkValidateReviewDelete(review);

        final Store store = review.getReservation().getStore();

        store.adjustReviewRatingByRemove(review);

        reviewQuickReplyRepository.deleteByReviewId(reviewId);
        reviewRepository.deleteById(reviewId);

        return reviewId;
    }

    @Transactional
    public Long changeReview(final Long reviewId, final UpdateReviewRequest updateReviewRequest, final String username, final String imageUrl){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(UserException.USER_NOT_FOUND));
        final Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new GeneralException(RentalException.REVIEW_NOT_FOUND));

        checkReviewOwner(loginUser, review);

        final Store store = review.getReservation().getStore();

        store.changeReviewRatingAndRecalculatingAverage(review.getRating(),updateReviewRequest.getRating());

        reviewQuickReplyRepository.deleteByReviewId(reviewId);
        saveQuickReply(updateReviewRequest.getQuickReplyIds(), review);

        review.changeContentAndRatingAndImageUrl(updateReviewRequest.getComment(),
                updateReviewRequest.getRating(), imageUrl);

        return reviewId;
    }

    private void checkValidateReviewDelete(final Review review) {

        final LocalDateTime now = LocalDateTime.now();

        // 리뷰 작성일이 현재로부터 7일 이내인지 확인
        if (review.getCreatedAt().isAfter(now.minusDays(7))) {
            throw new GeneralException(RentalException.CANT_DELETE_REVIEW_IN_7_DAYS);
        }
    }

    private void saveQuickReply(final List<Long> quickReplyIds, final Review review){

        quickReplyIds.forEach(quickReplyId->{
            final QuickReply quickReply = quickReplyRepository.findById(quickReplyId).orElseThrow(()->new GeneralException(RentalException.CANT_FIND_QUICK_REPLY));
            reviewQuickReplyRepository.save(ReviewQuickReply.of(review, quickReply));
        });
    }

    private void checkWriteReview(final User loginUser, final Reservation reservation){

        if(!loginUser.getId().equals(reservation.getUser().getId())){
            throw new GeneralException(RentalException.CANT_ACCESS_TO_OTHER_RESERVATION);
        }

        boolean alreadyExistReview = reviewRepository.existsByReservationId(reservation.getId());

        if(alreadyExistReview){
            throw new GeneralException(RentalException.CANT_WRITE_REVIEW_IN_SAME_RESERVATION);
        }
    }

    private void checkReviewOwner(final User loginUser, final Review review){

        if(!loginUser.getId().equals(review.getReservation().getUser().getId())){
            throw new GeneralException(RentalException.CANT_ACCESS_TO_OTHER_REVIEW);
        }
    }

    public ShowReviewWithMetaResponse getReviewsResponse(final Long storeId, final Pageable pageable){

        Slice<Review> reviewSlice = reviewRepository.getReviewSlice(storeId, pageable);
        List<ReviewQuickReply> reviewQuickReplies = reviewQuickReplyRepository.findByReviewInWithFetchQuickReply(reviewSlice.getContent());

        final Map<Long, List<String>> reviewIdToQuickReplyNames = reviewQuickReplies.stream()
                .collect(Collectors.groupingBy(
                        rr -> rr.getReview().getId(),
                        Collectors.mapping(rr -> rr.getQuickReply().getName(), Collectors.toList())
                ));

        return ShowReviewWithMetaResponse.of(reviewSlice.getContent().stream()
                .map(review -> {

                    final User user = review.getReservation().getUser();
                    final Integer countOfVisit = reservationRepository.countByReservationAndStore(review.getReservation().getStore(), user);

                    final List<String> quickReplyNames = reviewIdToQuickReplyNames.getOrDefault(review.getId(), List.of());

                    final List<DeviceReservation> deviceReservations = review.getReservation().getDeviceReservations();

                    return ShowReviewResponse.from(review, countOfVisit, quickReplyNames, deviceReservations);
                }).toList(), reviewSlice.hasNext());
    }

    public ShowReviewMetaResponse getReviewMetaByStore(final Long storeId){

        final Store store = storeRepository.findById(storeId).orElseThrow(()->new GeneralException(
                StoreException.CANT_FIND_STORE));

        final List<ShowCountPerQuickReplyResponse> showCountPerQuickReplyResponses = quickReplyRepository.findAll().stream().map(quickReply -> {
            int countByQuickReply = reviewQuickReplyRepository.countByStoreAndQuickReply(store, quickReply);
            return ShowCountPerQuickReplyResponse.of(quickReply.getName(), countByQuickReply);
        }).toList();

        return ShowReviewMetaResponse.of(store.getReviewCount(), showCountPerQuickReplyResponses);


    }

    public ShowReviewOneResponse getReviewById(final Long reviewId){

        final Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new GeneralException(RentalException.REVIEW_NOT_FOUND));

        final List<DeviceReservation> deviceReservations = deviceReservationRepository.findByReservationIdWithFetchStoreDeviceAndDevice(review.getReservation().getId());

        final List<Long> reviewQuickReplyIds = reviewQuickReplyRepository.findByReviewWithFetchQuickReply(review).stream().map(reviewQuickReply -> reviewQuickReply.getQuickReply().getId()).toList();

        final Integer countOfVisit = reservationRepository.countByReservationAndStore(review.getReservation().getStore(), review.getReservation()
                .getUser());

        return ShowReviewOneResponse.from(review, deviceReservations, reviewQuickReplyIds, countOfVisit);
    }


}
