package com.TwoSeaU.BaData.domain.sos.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.TwoSeaU.BaData.global.sse.SseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final SseService sseService;

	@GetMapping("/sse/subscribe")
	public SseEmitter subscribe(@AuthenticationPrincipal User user) {
		return sseService.subscribe(user.getUsername());
	}
}