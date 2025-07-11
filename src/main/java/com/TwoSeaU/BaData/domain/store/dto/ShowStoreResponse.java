package com.TwoSeaU.BaData.domain.store.dto;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowStoreResponse {

    private Long id;
    private double longititude;
    private double latitude;
    private String name;
    private boolean isOpening;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Double distanceFromMe;
    private String detailAddress;
    private int leftDeviceCount;

    public static ShowStoreResponse from(final Store store,final Double distanceFromMe,final int leftDeviceCount){

        return ShowStoreResponse.builder()
                .id(store.getId())
                .longititude(store.getPosition().getY())
                .latitude(store.getPosition().getX())
                .name(store.getName())
                .openTime(store.getStartTime())
                .closeTime(store.getEndTime())
                .distanceFromMe(distanceFromMe)
                .detailAddress(store.getDetailAddress())
                .leftDeviceCount(leftDeviceCount)
                .build();
    }


}
