package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StoreDeviceCustomRepository {
    List<ShowStoreWithLeftDeviceResponse> findStoresInBoundingBox(final StoreMapSearchRequest storeMapSearchRequest);

    Slice<ShowStoreWithLeftDeviceAndDistanceResponse> findStoresByPage(final StoreSearchRequest storeSearchRequest, Pageable pageable);

    List<StoreDevice> findProperDevicesByStore(final DeviceSearchRequest deviceSearchRequest, final Long storeId);

}
