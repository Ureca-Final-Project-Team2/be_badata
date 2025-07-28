package com.TwoSeaU.BaData.domain.rental.controller;

import com.TwoSeaU.BaData.domain.rental.dto.request.RestockDeviceRequest;
import com.TwoSeaU.BaData.domain.rental.service.RestockService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restock")
public class RestockController {

    private final RestockService restockService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> restockStoreDevice(@RequestBody @Valid RestockDeviceRequest restockDeviceRequest,
                                                                @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(restockService.restockStoreDevice(restockDeviceRequest, user.getUsername())));
    }

    @DeleteMapping("/{restockId}")
    public ResponseEntity<ApiResponse<Long>> deleteRestock(@PathVariable("restockId") Long restockId,
                                                           @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(restockService.deleteRestock(restockId, user.getUsername())));
    }

}
