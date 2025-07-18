package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.TwoSeaU.BaData.domain.user.entity.User;
import java.time.LocalDateTime;
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

    private Long userId;

    private String comment;

    private LocalDateTime createdAt;

    private Integer rating;

    private String imageUrl;

    public static ShowReviewResponse from(final Review review){

        return ShowReviewResponse.builder()
                .reviewId(review.getId())
                .userId(review.getReservation().getUser().getId())
                .comment(review.getContent())
                .createdAt(review.getCreatedAt())
                .imageUrl(review.getImageUrl())
                .rating(review.getRating())
                .build();
    }

}
