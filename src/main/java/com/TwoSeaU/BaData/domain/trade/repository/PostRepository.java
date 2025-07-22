package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {
    List<Post> findByIsSoldOrderByCreatedAtDesc(final boolean isSold);
    List<Post> findByIsSoldAndSellerIdOrderByCreatedAtDesc(final boolean isSold, final Long sellerId);
    List<Post> findByDeadLineBefore(final LocalDate time);
    List<Post> findByTitleContaining(final String query);
}
