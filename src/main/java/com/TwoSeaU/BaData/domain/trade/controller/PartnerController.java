package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetPartnerResponse;
import com.TwoSeaU.BaData.domain.trade.service.PartnerService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class PartnerController {
    private final PartnerService partnerService;

    @GetMapping("/partners/{category}")
    public ResponseEntity<ApiResponse<GetPartnerResponse>> getPartners(@PathVariable Long category) {
        return ResponseEntity.ok().body(ApiResponse.success(partnerService.getPartners(category)));
    }
}