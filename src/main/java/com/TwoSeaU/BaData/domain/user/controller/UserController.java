package com.TwoSeaU.BaData.domain.user.controller;

import com.TwoSeaU.BaData.domain.user.dto.response.CreateFollowResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;

import com.TwoSeaU.BaData.domain.user.dto.response.GetFollowsResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesStoreResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetPurchaseResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRentalResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetReportResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRestockResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSosResponse;
import com.TwoSeaU.BaData.domain.user.enums.FollowType;
import com.TwoSeaU.BaData.domain.user.service.UserService;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
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

	@GetMapping("/reports")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetReportResponse>>> getAllReportsByCursor(
		@RequestParam ReportStatus reportStatus,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllReportsByCursor(reportStatus, cursor, size, user.getUsername())));
	}

	@GetMapping("/purchases")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetPurchaseResponse>>> getAllPurchasesByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllPurchasesByCursor(cursor, size, user.getUsername())));
	}

	@GetMapping("/likes/posts")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetLikesPostResponse>>> getAllLikesPostsByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllLikesPostsByCursor(cursor, size, user.getUsername())));
	}

	@GetMapping("/sales")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetSaleResponse>>> getAllSalesByCursor(
		@RequestParam(required = false) PostCategory postCategory,
		@RequestParam(defaultValue = "false") Boolean isSold,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllSalesByCursor(postCategory, isSold, cursor, size, user.getUsername())));
	}

	@GetMapping("/sos")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetSosResponse>>> getAllSosByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllSosByCursor(cursor, size, user.getUsername())));
	}

	@PostMapping("/{userId}/follows")
	public ResponseEntity<ApiResponse<CreateFollowResponse>> createFollow(@PathVariable("userId") Long userId, @AuthenticationPrincipal User user){

		return ResponseEntity.ok().body(ApiResponse.success(userService.createFollow(userId, user.getUsername())));
	}

	@GetMapping("/follows")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetFollowsResponse>>> getFollowsByCursor(
		@RequestParam(defaultValue = "FOLLOWERS") FollowType followType,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getFollowsByCursor(followType, cursor, size, user.getUsername())));
	}

	@GetMapping("/likes/stores")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetLikesStoreResponse>>> getAllLikesStoresByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllLikesStoresByCursor(cursor, size, user.getUsername())));
	}

	@GetMapping("/rentals")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetRentalResponse>>> getAllRentalsByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllRentalsByCursor(cursor, size, user.getUsername())));
	}

	@GetMapping("/restock")
	public ResponseEntity<ApiResponse<CursorPageResponse<GetRestockResponse>>> getAllRestocksByCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(userService.getAllRestocksByCursor(cursor, size, user.getUsername())));
	}
}
