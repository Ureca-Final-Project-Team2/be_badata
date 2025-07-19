package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.ReviewQuickReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewQuickReplyRepository extends JpaRepository<ReviewQuickReply,Long> {

    @Modifying
    @Query("DELETE FROM ReviewQuickReply rqr WHERE rqr.review.id =:reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
}
