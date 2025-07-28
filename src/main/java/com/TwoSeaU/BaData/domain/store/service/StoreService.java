package com.TwoSeaU.BaData.domain.store.service;

import com.TwoSeaU.BaData.domain.store.dto.projection.StoreWithDistanceProjection;
import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDetailResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreLikesRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.util.List;
import java.util.Set;
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
    private final StoreLikesRepository storeLikesRepository;
    private final UserRepository userRepository;

    public List<ShowStoreMapResponse> getStoreMapResponse(final StoreMapSearchRequest storeMapSearchRequest, final String username){

        return storeDeviceRepository.findStoresInBoundingBox(storeMapSearchRequest, username).stream().map(
                showStoreWithLeftDeviceResponse ->
            ShowStoreMapResponse.from(showStoreWithLeftDeviceResponse.getStore(), showStoreWithLeftDeviceResponse.getLeftDeviceCount(), showStoreWithLeftDeviceResponse.isLiked())
        ).toList();
    }

    public ShowStoreWithMetaResponse getStoresResponse(final StoreSearchRequest storeSearchRequest,final
            Pageable pageable, final String username){

        Set<Long> userLikedStore = storeLikesRepository.getUserLikedStoreIds(username);

        Slice<ShowStoreWithLeftDeviceAndDistanceResponse> storesWithSlice = storeDeviceRepository.findStoresByPage(storeSearchRequest,pageable);

        return ShowStoreWithMetaResponse.of(storesWithSlice.getContent().stream().map(
                showStoreWithLeftDeviceAndDistanceResponse ->
            ShowStoreResponse.from(showStoreWithLeftDeviceAndDistanceResponse.getStore(), showStoreWithLeftDeviceAndDistanceResponse.getDistance(), showStoreWithLeftDeviceAndDistanceResponse.getLeftDeviceCount(),userLikedStore)
        ).toList(),storesWithSlice.hasNext());

    }

    public List<ShowDeviceInfoResponse> getStoreDeviceResponse(final DeviceSearchRequest deviceSearchRequest, final Long storeId){

        if(!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        return storeDeviceRepository.findProperDevicesByStore(deviceSearchRequest,storeId)
                .stream().map(ShowDeviceInfoResponse::from).toList();

    }

    public ShowStoreDetailResponse getStoreDetail(final Long storeId, final Double centerLat,final Double centerLng, final String username){

        if(!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        final StoreWithDistanceProjection storeWithDistance = storeRepository.findStoreWithDistance(
                storeId, centerLat, centerLng);

        if(username == null){
            return ShowStoreDetailResponse.from(storeWithDistance,false);
        }

        final User user = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.USER_NOT_FOUND));

        return ShowStoreDetailResponse.from(storeWithDistance,
                storeLikesRepository.existsByUserIdAndStoreId(user.getId(), storeId));

    }

}
