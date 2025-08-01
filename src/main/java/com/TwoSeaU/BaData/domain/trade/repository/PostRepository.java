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
    int countBySellerId(final Long sellerId);
    List<Post> findTop5ByIsSoldAndIsDeletedOrderByCreatedAtDesc(final boolean isSold, final boolean isDeleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithLock(@Param("postId") final Long postId);
}
