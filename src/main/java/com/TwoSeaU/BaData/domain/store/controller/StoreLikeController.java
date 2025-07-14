package com.TwoSeaU.BaData.domain.store.controller;

import com.TwoSeaU.BaData.domain.store.service.StoreLikeService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreLikeController {

    private final StoreLikeService storeLikeService;

    @PostMapping("/{storeId}/like")
    public ResponseEntity<ApiResponse<Long>> likeStore(@AuthenticationPrincipal User user,
                                                       @PathVariable("storeId") Long storeId){

        return ResponseEntity.ok(ApiResponse.success(storeLikeService.likeStore(user.getUsername(), storeId)));
    }

}
