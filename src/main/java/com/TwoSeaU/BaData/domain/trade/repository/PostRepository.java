package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {
    List<Post> findByIsSoldAndIsDeletedOrderByCreatedAtDesc(final boolean isSold, final boolean isDeleted);
    List<Post> findByIsSoldAndSellerIdAndIsDeletedOrderByCreatedAtDesc(final boolean isSold, final Long sellerId, final boolean isDeleted);
    List<Post> findByDeadLineBetweenAndIsDeleted(LocalDate start, LocalDate end, boolean isDeleted);
    List<Post> findByIsDeletedAndTitleContaining(final boolean isDeleted, final String query);
}
