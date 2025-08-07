package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveRecommendLikesResponse;
import com.TwoSeaU.BaData.domain.trade.service.LikeService;
import com.TwoSeaU.BaData.domain.trade.service.recommend.GlobalRecommendService;
import com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector.RecommendServiceDouble;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.RecommendServiceFloat;
import com.TwoSeaU.BaData.domain.trade.service.recommend.pgVector.RecommendServicePg;
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
    private final RecommendServiceDouble doubleRecommendServiceDouble;
    private final RecommendServiceFloat floatRecommendServiceFloat;
    private final RecommendServicePg pgvectorRecommendServicePg;
    private final GlobalRecommendService globalRecommendService;
    private final LikeService likeService;

    @GetMapping("/double")
    public ResponseEntity<ApiResponse<PostsResponse>> recommendPostsByDouble(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "true") boolean isStart) {
        return ResponseEntity.ok().body(ApiResponse.success(doubleRecommendServiceDouble.recommendPosts(user == null ? null : user.getUsername(), isStart)));
    }

    @GetMapping("/float")
    public ResponseEntity<ApiResponse<PostsResponse>> recommendPostsByFloat(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "true") boolean isStart) {
        return ResponseEntity.ok().body(ApiResponse.success(floatRecommendServiceFloat.recommendPostsByFloat(user == null ? null : user.getUsername(), isStart)));
    }

    @GetMapping("/pgvector")
    public ResponseEntity<ApiResponse<PostsResponse>> recommendPostsByPgvector(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "true") boolean isStart) {
        return ResponseEntity.ok().body(ApiResponse.success(pgvectorRecommendServicePg.recommendPostsBypgVector(user == null ? null : user.getUsername(), isStart)));
    }

    @PostMapping("/likes/{postId}")
    public ResponseEntity<ApiResponse<SaveRecommendLikesResponse>> likeRecommendation(@AuthenticationPrincipal User user, @PathVariable Long postId) {
        return ResponseEntity.ok().body(ApiResponse.success(likeService.likesAtRecommendation(user == null ? null : user.getUsername(), postId)));
    }

    @PatchMapping("/vector/update/all/double")
    public ResponseEntity<ApiResponse<String>> updateAllPostForDouble() {
        return ResponseEntity.ok().body(ApiResponse.success(globalRecommendService.updateAllDoubleVector()));
    }

    @PatchMapping("/vector/update/all/float")
    public ResponseEntity<ApiResponse<String>> updateAllPostVectorForPgvector() {
        return ResponseEntity.ok().body(ApiResponse.success(globalRecommendService.updateAllFloatAndPgVector()));
    }
}
