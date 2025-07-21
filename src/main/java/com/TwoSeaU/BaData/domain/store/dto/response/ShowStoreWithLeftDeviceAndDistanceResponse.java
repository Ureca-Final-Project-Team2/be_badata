package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowStoreWithLeftDeviceAndDistanceResponse {

    private Store store;
    private Double distance;
    private Integer leftDeviceCount;

}
