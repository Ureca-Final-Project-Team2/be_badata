package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.dto.projection.StoreWithDistanceProjection;
import com.TwoSeaU.BaData.domain.store.entity.Store;
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
public class ShowStoreDetailResponse {

    private Long storeId;
    private String name;
    private String imageUrl;
    private String detailAddress;
    private String phoneNumber;
    private Double distanceFromMe;
    private Double reviewRating;
    private Boolean isOpening;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isLiked;

    public static ShowStoreDetailResponse from(final StoreWithDistanceProjection storeWithDistanceProjection,final boolean isLiked){

        Boolean isOpening = LocalTime.now().isAfter(storeWithDistanceProjection.getStartTime())&& LocalTime.now().isBefore(storeWithDistanceProjection.getEndTime())?
                            true : false;

        return ShowStoreDetailResponse.builder()
                .storeId(storeWithDistanceProjection.getStoreId())
                .name(storeWithDistanceProjection.getName())
                .imageUrl(storeWithDistanceProjection.getImageUrl())
                .detailAddress(storeWithDistanceProjection.getDetailAddress())
                .phoneNumber(storeWithDistanceProjection.getPhoneNumber())
                .reviewRating(storeWithDistanceProjection.getReviewRating())
                .isOpening(isOpening)
                .startTime(storeWithDistanceProjection.getStartTime())
                .endTime(storeWithDistanceProjection.getEndTime())
                .distanceFromMe(storeWithDistanceProjection.getDistanceFromMe())
                .isLiked(isLiked)
                .build();
    }



}
