package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import java.util.List;

public interface StoreDeviceCustomRepository {
    List<Store> findStoresInBoundingBox(final StoreSearchRequest storeSearchRequest);

}
