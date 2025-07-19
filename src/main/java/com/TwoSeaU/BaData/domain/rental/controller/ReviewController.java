package com.TwoSeaU.BaData.domain.rental.controller;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.UpdateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReviewWithMetaResponse;
import com.TwoSeaU.BaData.domain.rental.service.ReviewService;
import com.TwoSeaU.BaData.domain.rental.service.ReviewUploadFacade;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewUploadFacade reviewUploadFacade;
    private final ReviewService reviewService;

    @PostMapping(value = "/api/v1/reviews",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createReview(@ModelAttribute @Valid CreateReviewRequest createReviewRequest,
                                                          @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(reviewUploadFacade.uploadReviewWithImage(createReviewRequest, user.getUsername())));
    }

    @GetMapping("/api/v1/{storeId}/reviews")
    public ResponseEntity<ApiResponse<ShowReviewWithMetaResponse>> getReviewsResponse(@PathVariable("storeId") final Long storeId, final
    Pageable pageable){

        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewsResponse(storeId, pageable)));
    }

    @DeleteMapping("/api/v1/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Long>> deleteReview(@PathVariable("reviewId") final Long reviewId, @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.success(reviewService.deleteReview(reviewId,user.getUsername())));
    }

    @PatchMapping(value = "/api/v1/reviews/{reviewId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> updateReview(@PathVariable("reviewId") final Long reviewId,
                                                          @AuthenticationPrincipal User user,
                                                          @ModelAttribute @Valid UpdateReviewRequest updateReviewRequest){

        return ResponseEntity.ok(ApiResponse.success(reviewUploadFacade.changeReviewWithImage(reviewId, updateReviewRequest,
                user.getUsername())));
    }

}
