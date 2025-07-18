package com.TwoSeaU.BaData.domain.rental.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewRequest {

    private Long reservationId;

    private Long quickReplyId;

    private String comment;

    private Integer rating;

}
