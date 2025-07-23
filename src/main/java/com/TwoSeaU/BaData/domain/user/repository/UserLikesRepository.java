package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.TwoSeaU.BaData.domain.user.entity.UserLikes;

public interface UserLikesRepository extends JpaRepository<UserLikes, Long>, UserLikesQueryRepository {

    Optional<UserLikes> findByFollowerUserAndFollowingUser(final User follower,final User followingUser);

}
