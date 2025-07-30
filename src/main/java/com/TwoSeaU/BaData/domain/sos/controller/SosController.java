package com.TwoSeaU.BaData.domain.sos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TwoSeaU.BaData.domain.sos.dto.response.RespondSosResponse;
import com.TwoSeaU.BaData.domain.sos.service.SosService;
import com.TwoSeaU.BaData.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sos")
public class SosController {

	private final SosService sosService;
	private final SimpMessagingTemplate template;

	@MessageMapping("/request")
	public void requestSos(@AuthenticationPrincipal User user) {
		sosService.requestSos(user.getUsername());

		template.convertAndSend(
			"/topic/request",
			"누군가 데이터 SOS를 요청하였습니다."
		);
	}

	@PostMapping("/{sosId}/respond")
	public ResponseEntity<ApiResponse<RespondSosResponse>> respondSos(@PathVariable Long sosId, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok().body(ApiResponse.success(sosService.respondSos(sosId, user.getUsername())));
	}
}