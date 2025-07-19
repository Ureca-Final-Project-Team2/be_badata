package com.TwoSeaU.BaData.domain.user.controller;

import com.TwoSeaU.BaData.domain.user.dto.request.AddressCreateRequest;
import com.TwoSeaU.BaData.domain.user.service.AddressService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createAddress(@RequestBody @Valid final AddressCreateRequest addressCreateRequest,
                                                           @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(addressService.createAddress(addressCreateRequest, user.getUsername())));
    }

}
