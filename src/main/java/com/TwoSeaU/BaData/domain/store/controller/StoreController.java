package com.TwoSeaU.BaData.domain.store.controller;

import com.TwoSeaU.BaData.domain.store.dto.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.StoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.service.StoreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/tmp")
    public List<StoreResponse> getStoreResponse(){

        return storeService.findList();
    }

    @GetMapping("/map")
    public List<ShowStoreMapResponse> getStoreMapResponse(@ModelAttribute StoreMapSearchRequest storeMapSearchRequest){

        return storeService.getStoreMapResponse(storeMapSearchRequest);
    }

    @GetMapping
    public ShowStoreWithMetaResponse getStoresResponse(@ModelAttribute StoreSearchRequest storeSearchRequest, Pageable pageable){

        return storeService.getStoresResponse(storeSearchRequest,pageable);
    }

}
