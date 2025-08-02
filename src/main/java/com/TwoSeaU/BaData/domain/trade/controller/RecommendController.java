package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveRecommendLikesResponse;
import com.TwoSeaU.BaData.domain.trade.service.LikeService;
import com.TwoSeaU.BaData.domain.trade.service.RecommendService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades/posts/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PostsResponse>> recommendPosts(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "true") boolean isStart) {
        return ResponseEntity.ok().body(ApiResponse.success(recommendService.recommendPosts(user == null ? null : user.getUsername(), isStart)));
    }

    @PostMapping("/likes/{postId}")
    public ResponseEntity<ApiResponse<SaveRecommendLikesResponse>> likeRecommendation(@AuthenticationPrincipal User user, @PathVariable Long postId) {
        return ResponseEntity.ok().body(ApiResponse.success(likeService.likesAtRecommendation(user == null ? null : user.getUsername(), postId)));
    }

    @PatchMapping("/vector/update")
    public ResponseEntity<ApiResponse<Integer>> updatePostVector() {
        return ResponseEntity.ok().body(ApiResponse.success(recommendService.updatePostVector()));
    }
}
