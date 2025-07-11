package com.TwoSeaU.BaData.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;

import com.TwoSeaU.BaData.domain.user.dto.response.GetAllLikesPostsResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllPurchasesResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAllReportResponse;
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

	@GetMapping("/reports/complete")
	public ResponseEntity<ApiResponse<GetAllReportResponse>> getCompleteReports(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getCompleteReports(user.getUsername())));
	}

	@GetMapping("/reports/pending")
	public ResponseEntity<ApiResponse<GetAllReportResponse>> getPendingReports(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getPendingReports(user.getUsername())));
	}

	@GetMapping("/purchases")
	public ResponseEntity<ApiResponse<GetAllPurchasesResponse>> getAllPurchases(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllPurchasesResponse(user.getUsername())));
	}

	@GetMapping("/likes/posts")
	public ResponseEntity<ApiResponse<GetAllLikesPostsResponse>> getAllLikesPosts(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllLikesPosts(user.getUsername())));
	}
}
