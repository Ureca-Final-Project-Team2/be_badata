package com.TwoSeaU.BaData.domain.sos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.sos.dto.SaveSosResponse;
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
}