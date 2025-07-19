package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.CreateReviewRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.UpdateReviewRequest;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewUploadFacade {

    private final S3ImageService s3ImageService;
    private final ReviewService reviewService;
    private static final String reviewDirectory = "reviews/";

    public Long uploadReviewWithImage(final CreateReviewRequest createReviewRequest, final String username){

        final String imageUrl = cantGetImageUrl(createReviewRequest.getFile()) ? null : s3ImageService.saveImage(createReviewRequest.getFile(),
                                                                                                reviewDirectory,
                                                                                                 createReviewRequest.getFile().getOriginalFilename());

        return reviewService.createReview(createReviewRequest, username, imageUrl);
    }

    public Long changeReviewWithImage(final Long reviewId, final UpdateReviewRequest updateReviewRequest, final String username){

        final String imageUrl = cantGetImageUrl(updateReviewRequest.getFile()) ? null : s3ImageService.saveImage(updateReviewRequest.getFile(),
                reviewDirectory,
                updateReviewRequest.getFile().getOriginalFilename());

        return reviewService.changeReview(reviewId, updateReviewRequest, username, imageUrl);
    }

    final boolean cantGetImageUrl(final MultipartFile file){

        if (file ==null || file.isEmpty()){
            return true;
        }

        return false;
    }

}
