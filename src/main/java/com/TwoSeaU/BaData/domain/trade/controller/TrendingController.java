package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.service.TrendingService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades/search/trending")
public class TrendingController {
    private final TrendingService trendingService;

    @GetMapping
    public ResponseEntity<ApiResponse<GetTrendingResponse>> getTrendingKeyword() {
        return ResponseEntity.ok().body(ApiResponse.success(trendingService.getTrendingKeyword()));
    }
}
