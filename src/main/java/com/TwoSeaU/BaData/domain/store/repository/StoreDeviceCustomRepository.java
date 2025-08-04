package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDeviceWithRemainCountResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StoreDeviceCustomRepository {
    List<ShowStoreWithLeftDeviceResponse> findStoresInBoundingBox(final StoreMapSearchRequest storeMapSearchRequest, final String username);

    Slice<ShowStoreWithLeftDeviceAndDistanceResponse> findStoresByPage(final StoreSearchRequest storeSearchRequest, final Pageable pageable);

    List<ShowStoreDeviceWithRemainCountResponse> findProperDevicesByStore(final DeviceSearchRequest deviceSearchRequest, final Long storeId);

    List<ShowStoreMapResponse> findClustersDynamically(StoreMapSearchRequest request,int eps, int minPoints);

}
