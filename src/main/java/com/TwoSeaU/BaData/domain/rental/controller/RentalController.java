package com.TwoSeaU.BaData.domain.rental.controller;

import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservationDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.rental.service.RentalService;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/{storeId}/devices")
    public ResponseEntity<ApiResponse<List<ShowReservationDeviceInfoResponse>>> getStoreDetailDeviceResponse(
            @RequestParam("rentalStartDate") LocalDateTime rentalStartDate,
            @RequestParam("rentalEndDate") LocalDateTime rentalEndDate,
            @PathVariable("storeId") Long storeId){

        return ResponseEntity.ok(ApiResponse.success(rentalService.getReservationDeviceInfoResponse(rentalStartDate,rentalEndDate,storeId)));

    }

}
