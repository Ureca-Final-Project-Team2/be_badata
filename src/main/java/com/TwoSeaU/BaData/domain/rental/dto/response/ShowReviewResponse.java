package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.TwoSeaU.BaData.domain.user.entity.User;
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
public class ShowReviewResponse {

    private Long reviewId;

    private Long writerId;

    private String name;

    private String userImageUrl;

    private String reviewImageUrl;

    private String comment;

    private LocalDateTime createdAt;

    private Integer rating;

    private Integer countOfVisit;

    private List<String> quickReplyNames = new ArrayList<>();

    public static ShowReviewResponse from(final Review review, final Integer countOfVisit, final List<String> quickReplyName){

        return ShowReviewResponse.builder()
                .reviewId(review.getId())
                .writerId(review.getReservation().getUser().getId())
                .name(review.getReservation().getUser().getNickName())
                .userImageUrl(review.getReservation().getUser().getProfileImageUrl())
                .reviewImageUrl(review.getImageUrl())
                .comment(review.getContent())
                .createdAt(review.getCreatedAt())
                .rating(review.getRating())
                .countOfVisit(countOfVisit)
                .quickReplyNames(quickReplyName)
                .build();
    }

}
