package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveReportRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.SaveReportResponse;
import com.TwoSeaU.BaData.domain.trade.service.ReportService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("{postId}/reports")
    public ResponseEntity<ApiResponse<SaveReportResponse>> createReport(@PathVariable Long postId, @Valid @RequestBody SaveReportRequest saveReportRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(reportService.createReport(postId, saveReportRequest, user.getUsername())));
    }

}
