package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long>, PostLikesQueryRepository{
    Optional<PostLikes> findByUserIdAndPostId(final Long userId, final Long postId);
    Boolean existsByUserIdAndPostId(final Long userId, final Long postId);
    int countByPostId(final Long postId);
    int countByUserId(final Long userId);
}
