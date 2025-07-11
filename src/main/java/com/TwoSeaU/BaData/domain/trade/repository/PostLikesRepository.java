package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {
    Optional<PostLikes> findByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(Long postId);
    List<PostLikes> findAllByUserId(Long userId);
}
