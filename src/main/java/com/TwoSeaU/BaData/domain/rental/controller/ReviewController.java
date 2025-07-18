package com.TwoSeaU.BaData.domain.rental.controller;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowRentalResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewWithMetaResponse;
import com.TwoSeaU.BaData.domain.rental.service.ReviewService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/api/v1/reviews",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createReview(@ModelAttribute CreateReviewRequest createReviewRequest,
                                                          @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(reviewService.createReview(createReviewRequest,user.getUsername())));
    }

}
