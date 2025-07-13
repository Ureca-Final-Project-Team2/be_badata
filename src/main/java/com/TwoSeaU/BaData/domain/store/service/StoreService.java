package com.TwoSeaU.BaData.domain.store.service;

import static com.TwoSeaU.BaData.domain.store.entity.QStoreDevice.storeDevice;

import com.TwoSeaU.BaData.domain.store.dto.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.StoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.StoreWithRemainDto;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.querydsl.core.Tuple;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreDeviceRepository storeDeviceRepository;

    public List<StoreResponse> findList(){

        return storeRepository.findStoresWithDistance(37.577613288258206,126.97689786832184,10000).stream().map(StoreResponse::from).toList();
    }

    public List<ShowStoreMapResponse> getStoreMapResponse(final StoreMapSearchRequest storeMapSearchRequest){

        return storeDeviceRepository.findStoresInBoundingBox(storeMapSearchRequest).stream().map(
                ShowStoreMapResponse::from).toList();
    }

    public ShowStoreWithMetaResponse getStoresResponse(final StoreSearchRequest storeSearchRequest,final
            Pageable pageable){

        Slice<StoreWithRemainDto> storesWithSlice = storeDeviceRepository.findStoresByPage(storeSearchRequest,pageable);

        return ShowStoreWithMetaResponse.of(storesWithSlice.getContent().stream().map(storeWithRemainDto -> {
            return ShowStoreResponse.from(storeWithRemainDto.getStore(), storeWithRemainDto.getDistance(), storeWithRemainDto.getRemainingCount());
        }).toList(),storesWithSlice.hasNext());

    }

}
