package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowReviewOneResponse {

    private Long reviewId;
    private Long writerId;
    private Long reservationId;
    private int rating;
    private String comment;
    private String reviewImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> quickReplyIds = new ArrayList<>();
    private ShowRentalResponse showRentalResponses;



    public static ShowReviewOneResponse of(final Review review, final List<DeviceReservation> deviceReservations, final List<Long> reviewQuickReplyIds,
                                           final Integer countOfVisit){

        return ShowReviewOneResponse.builder()
                .reviewId(review.getId())
                .writerId(review.getReservation().getUser().getId())
                .reservationId(review.getReservation().getId())
                .rating(review.getRating())
                .comment(review.getContent())
                .reviewImageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .quickReplyIds(reviewQuickReplyIds)
                .showRentalResponses(ShowRentalResponse.of(review.getReservation().getStore(), deviceReservations.stream().map(deviceReservation ->
                     ShowReservedDeviceResponse.from(deviceReservation.getStoreDevice(),deviceReservation, review.getReservation())
                ).toList(), countOfVisit))
                .build();

    }

}
