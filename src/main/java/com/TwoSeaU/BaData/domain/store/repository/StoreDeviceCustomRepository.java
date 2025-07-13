package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.StoreWithRemainDto;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.querydsl.core.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StoreDeviceCustomRepository {
    List<Store> findStoresInBoundingBox(final StoreMapSearchRequest storeMapSearchRequest);

    Slice<StoreWithRemainDto> findStoresByPage(final StoreSearchRequest storeSearchRequest, Pageable pageable);

}
