package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.service.PostService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class TradeController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PostsResponse>> getPosts(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.success(postService.searchPosts(query)));
        }

        return ResponseEntity.ok().body(ApiResponse.success(postService.findAllPosts()));
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<ApiResponse<PostsResponse>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByUserId(userId)));
    }

    @GetMapping("/posts/deadline")
    public ResponseEntity<ApiResponse<PostsResponse>> getPostsByDeadLine() {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByDeadLine()));
    }
}
