package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.entity.QuickReply;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.TwoSeaU.BaData.domain.rental.entity.ReviewQuickReply;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.QuickReplyRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReviewQuickReplyRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReviewRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Long createReview(final CreateReviewRequest createReviewRequest, final String username){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.USER_NOT_FOUND));

        final Reservation reservation = reservationRepository.findById(createReviewRequest.getReservationId())
                        .orElseThrow(()->new GeneralException(RentalException.RESERVATION_NOT_FOUND));

        checkWriteReview(loginUser, reservation);

        final Review review = reviewRepository.save(Review.of(reservation, createReviewRequest.getComment(),createReviewRequest.getRating(),"www.image.url"));

        final QuickReply quickReply = quickReplyRepository.findById(createReviewRequest.getQuickReplyId()).orElseThrow(()->new GeneralException(RentalException.CANT_FIND_QUICK_REPLY));

        reviewQuickReplyRepository.save(ReviewQuickReply.of(review, quickReply));

        reservation.getStore().adjustReviewRatingByAdd(review);

        return review.getId();
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
}
