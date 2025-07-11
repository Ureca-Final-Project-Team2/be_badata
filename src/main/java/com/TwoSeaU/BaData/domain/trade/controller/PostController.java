package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.UserPostsResponse;
import com.TwoSeaU.BaData.domain.trade.service.PostService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PostsResponse>> getPosts(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.success(postService.searchPosts(query)));
        }

        return ResponseEntity.ok().body(ApiResponse.success(postService.findAllPosts()));
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<ApiResponse<UserPostsResponse>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByUserId(userId)));
    }

    @GetMapping("/posts/deadline")
    public ResponseEntity<ApiResponse<PostsResponse>> getPostsByDeadLine() {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByDeadLine()));
    }

    @PostMapping("/posts/gifticon")
    public ResponseEntity<ApiResponse<SavePostResponse>> createGifticonPost(@RequestBody SaveGifticonPostRequest saveGifticonPostRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.createGifticonPost(saveGifticonPostRequest, user.getUsername())));
    }

    @PostMapping("/posts/data")
    public ResponseEntity<ApiResponse<SavePostResponse>> createDataPost(@RequestBody SaveDataPostRequest saveDataPostRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.createDataPost(saveDataPostRequest, user.getUsername())));
    }
}
