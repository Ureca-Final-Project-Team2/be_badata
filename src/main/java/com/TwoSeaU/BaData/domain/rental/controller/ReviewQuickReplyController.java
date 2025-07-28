package com.TwoSeaU.BaData.domain.rental.controller;

import com.TwoSeaU.BaData.domain.rental.dto.response.ShowQuickReplyResponse;
import com.TwoSeaU.BaData.domain.rental.service.ReviewQuickReplyService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review-quick-replies")
public class ReviewQuickReplyController {

    private final ReviewQuickReplyService quickReplyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowQuickReplyResponse>>> getReviewQuickReplies(){

        return ResponseEntity.ok(ApiResponse.success(quickReplyService.getReviewQuickReplies()));
    }

}
