package com.TwoSeaU.BaData.domain.store.controller;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDetailResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.service.StoreService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<ShowStoreMapResponse>>> getStoreMapResponse(@ModelAttribute StoreMapSearchRequest storeMapSearchRequest,
                                                                                       @RequestParam int zoomLevel,
                                                                                       @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreMapResponse(storeMapSearchRequest, user == null ? null : user.getUsername(), zoomLevel)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShowStoreWithMetaResponse>> getStoresResponse(@ModelAttribute StoreSearchRequest storeSearchRequest,
                                                                                    @PageableDefault(size = 10, page = 0, sort = "distance", direction = Sort.Direction.ASC)
                                                                                    final Pageable pageable,
                                                                                    @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoresResponse(storeSearchRequest, pageable, user == null ? null : user.getUsername())));
    }

    @GetMapping("/{storeId}/devices")
    public ResponseEntity<ApiResponse<List<ShowDeviceInfoResponse>>> getStoreDeviceResponse(@ModelAttribute
            DeviceSearchRequest deviceSearchRequest, @PathVariable("storeId") Long storeId){

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreDeviceResponse(deviceSearchRequest,storeId)));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<ShowStoreDetailResponse>> getStoreDetailResponse(@PathVariable("storeId") Long storeId,
                                                                                       @RequestParam("centerLat") Double centerLat,
                                                                                       @RequestParam("centerLng") Double centerLng,
                                                                                       @AuthenticationPrincipal User user){

        final String username = user==null ? null : user.getUsername();

        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreDetail(storeId,centerLat,centerLng, username)));
    }


}
