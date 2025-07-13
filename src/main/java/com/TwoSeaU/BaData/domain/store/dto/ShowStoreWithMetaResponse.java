package com.TwoSeaU.BaData.domain.store.dto;

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
public class ShowStoreWithMetaResponse {

    private List<ShowStoreResponse> showStoreResponses = new ArrayList<>();
    private boolean hasNext;

    public static ShowStoreWithMetaResponse of(final List<ShowStoreResponse> showStoreResponses,
                                        final boolean hasNext){

        return ShowStoreWithMetaResponse.builder()
                .showStoreResponses(showStoreResponses)
                .hasNext(hasNext)
                .build();
    }



}
