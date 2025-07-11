package com.TwoSeaU.BaData.domain.store.service;

import com.TwoSeaU.BaData.domain.store.dto.StoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public List<StoreResponse> findList(){

        return storeRepository.findStoresWithDistance(37.577613288258206,126.97689786832184,10000).stream().map(StoreResponse::from).toList();
    }



}
