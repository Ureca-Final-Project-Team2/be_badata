package com.TwoSeaU.BaData.domain.store.dto;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreResponse {

    private Long id;
    private double longititude;
    private double latitude;
    private String name;

    public static StoreResponse from(final StoreWithDistanceProjection storeWithDistanceProjection){
        return StoreResponse.builder()
                .id(storeWithDistanceProjection.getId())
                .latitude(storeWithDistanceProjection.getLatitude())
                .longititude(storeWithDistanceProjection.getLongitude())
                .name(storeWithDistanceProjection.getName())
                .build();
    }
}
