package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShowStoreDeviceWithRemainCountResponse {

    private StoreDevice storeDevice;
    private Integer leftCount;

    public static ShowStoreDeviceWithRemainCountResponse of(final StoreDevice storeDevice, final Integer leftCount){

        return ShowStoreDeviceWithRemainCountResponse.builder()
                .storeDevice(storeDevice)
                .leftCount(leftCount)
                .build();
    }


}
