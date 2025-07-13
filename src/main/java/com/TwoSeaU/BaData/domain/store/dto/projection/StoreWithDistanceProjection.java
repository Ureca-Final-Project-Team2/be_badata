package com.TwoSeaU.BaData.domain.store.dto.projection;

import java.time.LocalTime;

public interface StoreWithDistanceProjection {

    Long getStoreId();
    String getName();
    String getImageUrl();
    String getDetailAddress();
    String getPhoneNumber();
    Double getDistanceFromMe();
    Double getReviewRating();
    LocalTime getStartTime();
    LocalTime getEndTime();
}
