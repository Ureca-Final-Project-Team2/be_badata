package com.TwoSeaU.BaData.domain.store.dto;

public interface StoreWithDistanceProjection {
    Long getId();
    String getName();
    Double getLatitude();
    Double getLongitude();
    Double getDistance();
}
