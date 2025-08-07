package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.service.PostService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class MockController {
    private final PostService postService;

    @PostMapping("/mock/posts")
    public ResponseEntity<ApiResponse<String>> createMockPost() {
        return ResponseEntity.ok().body(ApiResponse.success(postService.generateGifticons()));
    }
}
