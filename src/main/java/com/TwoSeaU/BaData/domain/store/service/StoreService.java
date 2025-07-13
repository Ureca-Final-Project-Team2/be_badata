package com.TwoSeaU.BaData.domain.store.service;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.StoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.StoreWithRemainDto;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
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

    public List<ShowDeviceInfoResponse> getStoreDeviceResponse(final DeviceSearchRequest deviceSearchRequest, final Long storeId){

        if(!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        return storeDeviceRepository.findProperDevicesByStore(deviceSearchRequest,storeId)
                .stream().map(ShowDeviceInfoResponse::from).toList();

    }

}
