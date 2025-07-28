package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.QuickReply;
import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.TwoSeaU.BaData.domain.rental.entity.ReviewQuickReply;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewQuickReplyRepository extends JpaRepository<ReviewQuickReply,Long> {

    @Modifying
    @Query("DELETE FROM ReviewQuickReply rqr WHERE rqr.review.id =:reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    @Query("select rqr From ReviewQuickReply rqr join fetch rqr.quickReply where rqr.review=:review")
    List<ReviewQuickReply> findByReviewWithFetchQuickReply(final Review review);

    @Query("select count(*) from ReviewQuickReply rqr join rqr.review rv join rv.reservation r where r.store=:store and rqr.quickReply=:quickReply")
    int countByStoreAndQuickReply(final Store store, final QuickReply quickReply);

    @Query("SELECT rr FROM ReviewQuickReply rr JOIN FETCH rr.quickReply WHERE rr.review IN :reviews")
    List<ReviewQuickReply> findByReviewInWithFetchQuickReply(@Param("reviews") List<Review> reviews);

}
