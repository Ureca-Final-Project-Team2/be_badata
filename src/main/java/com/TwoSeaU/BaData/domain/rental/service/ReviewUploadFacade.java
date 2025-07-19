package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewUploadFacade {

    private final S3ImageService s3ImageService;
    private final ReviewService reviewService;
    private static final String reviewDirectory = "reviews/";

    public Long uploadReviewWithImage(final CreateReviewRequest createReviewRequest, final String username){

        final String imageUrl = createReviewRequest.getFile()==null ? null : s3ImageService.saveImage(createReviewRequest.getFile(),
                                                                                                reviewDirectory,
                                                                                                 createReviewRequest.getFile().getOriginalFilename());

        return reviewService.createReview(createReviewRequest, username, imageUrl);
    }

}
