package com.TwoSeaU.BaData.domain.rental.dto.projection;

public interface AvailableDeviceProjection {
    Long getStoreDeviceId();

    Long getDeviceId();

    Integer getDataCapacity();
    String getDeviceName();

    Integer getPrice();

    String getImageUrl();
    Integer getAvailableCount();


}
