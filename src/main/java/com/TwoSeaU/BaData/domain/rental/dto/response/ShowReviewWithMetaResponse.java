package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
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
public class ShowReviewWithMetaResponse {

    private List<ShowReviewResponse> showReviewResponses = new ArrayList<>();
    private boolean hasNext;

    public static ShowReviewWithMetaResponse of(final List<ShowReviewResponse> showReviewResponses,
            final boolean hasNext){

        return ShowReviewWithMetaResponse.builder()
                .showReviewResponses(showReviewResponses)
                .hasNext(hasNext)
                .build();
    }

}
