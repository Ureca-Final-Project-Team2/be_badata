package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.service.TrendingKeywordService;
import com.TwoSeaU.BaData.domain.trade.service.TrendingPostService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades/")
public class TrendingController {
    private final TrendingPostService trendingPostService;
    private final TrendingKeywordService trendingKeywordService;

    @GetMapping("search/trending")
    public ResponseEntity<ApiResponse<GetTrendingResponse>> getTrendingKeyword() {
        return ResponseEntity.ok().body(ApiResponse.success(trendingKeywordService.getTrendingKeyword()));
    }

    @GetMapping("posts/trending")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getTrendingPosts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(trendingPostService.getTrendingPosts(user == null ? null : user.getUsername())));
    }
}
