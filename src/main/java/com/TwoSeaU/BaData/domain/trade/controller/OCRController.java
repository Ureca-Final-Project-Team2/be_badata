package com.TwoSeaU.BaData.domain.trade.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.TwoSeaU.BaData.domain.trade.service.OCRService;
import com.TwoSeaU.BaData.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vision")
public class OCRController {

	private final OCRService ocrService;

	@PostMapping("/getText")
	public ResponseEntity<ApiResponse<Map<String, String>>> detectText(
		@RequestParam("image")MultipartFile image,
		@AuthenticationPrincipal User user
	) {
		try {
			final Map<String, String> textList = ocrService.extractTextFromImageFile(image);
			return ResponseEntity.ok(ApiResponse.success(textList));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
