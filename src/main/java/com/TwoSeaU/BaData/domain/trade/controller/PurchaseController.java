package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetPurchaseGifticonDetailResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetPurchaseGifticonImageResponse;
import com.TwoSeaU.BaData.domain.trade.service.PurchaseService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades/purchases/")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping("{gifticonId}")
    public ResponseEntity<ApiResponse<GetPurchaseGifticonDetailResponse>> getGifticonDetail(@PathVariable Long gifticonId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(purchaseService.getGifticonDetail(gifticonId, user.getUsername())));
    }

    @GetMapping("{gifticonId}/image")
    public ResponseEntity<ApiResponse<GetPurchaseGifticonImageResponse>> getGifticonImage(@PathVariable Long gifticonId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(purchaseService.getGifticonImage(gifticonId, user.getUsername())));
    }
}
