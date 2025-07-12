package com.TwoSeaU.BaData.domain.store.controller;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.StoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.service.StoreService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResponse<List<ShowStoreMapResponse>>> getStoreMapResponse(@ModelAttribute StoreMapSearchRequest storeMapSearchRequest){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreMapResponse(storeMapSearchRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShowStoreWithMetaResponse>> getStoresResponse(@ModelAttribute StoreSearchRequest storeSearchRequest, Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoresResponse(storeSearchRequest,pageable)));
    }

    @GetMapping("/{storeId}/devices")
    public ResponseEntity<ApiResponse<List<ShowDeviceInfoResponse>>> getStoreDeviceResponse(@ModelAttribute
            DeviceSearchRequest deviceSearchRequest, @PathVariable("storeId") Long storeId){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreDeviceResponse(deviceSearchRequest,storeId)));
    }


}
