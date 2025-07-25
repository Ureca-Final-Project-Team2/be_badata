package com.TwoSeaU.BaData.domain.user.controller;

import com.TwoSeaU.BaData.domain.user.dto.request.FcmTokenRequest;
import com.TwoSeaU.BaData.domain.user.service.FcmTokenService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm-token")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> saveFcmToken(@AuthenticationPrincipal User user,
                                                          @RequestBody @Valid FcmTokenRequest fcmTokenRequest){

        return ResponseEntity.ok(ApiResponse.success(fcmTokenService.saveFcmToken(user.getUsername(), fcmTokenRequest)));
    }

}
