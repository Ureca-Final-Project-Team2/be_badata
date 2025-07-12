package com.TwoSeaU.BaData.domain.store.dto.response;

public interface StoreWithDistanceProjection {
    Long getId();
    String getName();
    Double getLatitude();
    Double getLongitude();
    Double getDistance();
}
