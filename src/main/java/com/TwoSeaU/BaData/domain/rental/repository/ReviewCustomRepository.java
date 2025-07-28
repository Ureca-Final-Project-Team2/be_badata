package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewCustomRepository {
    Slice<Review> getReviewSlice(final Long storeId, final Pageable pageable);

}
