package com.TwoSeaU.BaData.domain.rental.dto.response;

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
public class ShowReviewMetaResponse {

    private int reviewCount;

    private List<ShowCountPerQuickReplyResponse> showCountPerQuickReplyResponses = new ArrayList<>();

    public static ShowReviewMetaResponse of(final int reviewCount, final List<ShowCountPerQuickReplyResponse> showCountPerQuickReplyResponses){

        return ShowReviewMetaResponse.builder()
                .reviewCount(reviewCount)
                .showCountPerQuickReplyResponses(showCountPerQuickReplyResponses)
                .build();
    }

}
