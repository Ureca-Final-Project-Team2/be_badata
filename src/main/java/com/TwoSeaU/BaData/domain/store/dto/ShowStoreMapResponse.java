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
public class ShowStoreMapResponse {

    private Long id;
    private double longititude;
    private double latitude;
    private String name;

    public static ShowStoreMapResponse from(final Store store){
        return ShowStoreMapResponse.builder()
                .id(store.getId())
                .latitude(store.getPosition().getY())
                .longititude(store.getPosition().getX())
                .name(store.getName())
                .build();
    }
}
