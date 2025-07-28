package com.TwoSeaU.BaData.domain.sos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.sos.dto.response.RespondSosResponse;
import com.TwoSeaU.BaData.domain.sos.dto.response.SaveSosResponse;
import com.TwoSeaU.BaData.domain.sos.service.SosService;
import com.TwoSeaU.BaData.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sos")
public class SosController {

	private final SosService sosService;

	@PostMapping("/request")
	public ResponseEntity<ApiResponse<SaveSosResponse>> createSos(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(sosService.createSos(user.getUsername())));
	}

	@PostMapping("/{sosId}/respond")
	public ResponseEntity<ApiResponse<RespondSosResponse>> respondSos(@PathVariable Long sosId, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(sosService.respondSos(sosId, user.getUsername())));
	}
}