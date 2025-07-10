package com.TwoSeaU.BaData.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;

import com.TwoSeaU.BaData.domain.user.service.UserService;
import com.TwoSeaU.BaData.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;

	@GetMapping("/data")
	public ResponseEntity<ApiResponse<DataResponse>> getData(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getData(user.getUsername())));
	}

	@GetMapping("/coin")
	public ResponseEntity<ApiResponse<CoinResponse>> getCoin(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getCoin(user.getUsername())));
	}
}
