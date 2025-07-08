package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByIsSoldOrderByCreatedAtDesc(boolean isSold);
    List<Post> findByIsSoldAndSellerIdOrderByCreatedAtDesc(boolean isSold, Long sellerId);
    List<Post> findByDeadLineBefore(LocalDateTime time);
    List<Post> findByTitleContaining(String query);
}
