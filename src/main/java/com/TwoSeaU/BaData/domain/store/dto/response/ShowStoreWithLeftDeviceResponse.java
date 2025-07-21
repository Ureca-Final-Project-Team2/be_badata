package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowStoreWithLeftDeviceResponse {

    private Store store;
    private Integer remainingCount;
}
