package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.response.DeletePostLikesResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostLikesResponse;
import com.TwoSeaU.BaData.domain.trade.service.LikeService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("{postId}/likes")
    public ResponseEntity<ApiResponse<SavePostLikesResponse>> createLike(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(likeService.createLike(postId, user.getUsername())));
    }

    @DeleteMapping("{postId}/likes")
    public ResponseEntity<ApiResponse<DeletePostLikesResponse>> deleteLike(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(likeService.deleteLike(postId, user.getUsername())));
    }
}
