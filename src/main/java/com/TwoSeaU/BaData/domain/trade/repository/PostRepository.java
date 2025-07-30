package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {
    List<Post> findByIsSoldAndIsDeletedOrderByCreatedAtDesc(final boolean isSold, final boolean isDeleted);
    List<Post> findByIsSoldAndSellerIdAndIsDeletedOrderByCreatedAtDesc(final boolean isSold, final Long sellerId, final boolean isDeleted);
    List<Post> findByDeadLineBetweenAndIsDeleted(LocalDate start, LocalDate end, boolean isDeleted);
    List<Post> findByIsDeletedAndTitleContaining(final boolean isDeleted, final String query);
    int countBySellerId(final Long sellerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithLock(@Param("postId") final Long postId);
}
