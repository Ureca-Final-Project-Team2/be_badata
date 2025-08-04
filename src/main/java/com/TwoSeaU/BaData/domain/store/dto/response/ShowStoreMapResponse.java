package com.TwoSeaU.BaData.domain.store.dto.response;

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
    private int leftDeviceCount;
    private boolean isLiked;

    public static ShowStoreMapResponse from(final Store store, final int leftDeviceCount, final boolean isLiked){

        return ShowStoreMapResponse.builder()
                .id(store.getId())
                .latitude(store.getPosition().getY())
                .longititude(store.getPosition().getX())
                .name(store.getName())
                .leftDeviceCount(leftDeviceCount)
                .isLiked(isLiked)
                .build();
    }

    public static ShowStoreMapResponse of(final Long id, final double longititude, final double latitude, final String name, final int leftDeviceCount, final boolean isLiked){

        return ShowStoreMapResponse.builder()
                .id(id)
                .longititude(longititude)
                .latitude(latitude)
                .name(name)
                .leftDeviceCount(leftDeviceCount)
                .isLiked(isLiked)
                .build();
    }
}
