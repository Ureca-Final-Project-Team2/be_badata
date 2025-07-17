package com.TwoSeaU.BaData.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TwoSeaU.BaData.domain.user.entity.UserLikes;

public interface UserLikesRepository extends JpaRepository<UserLikes, Long>, UserLikesQueryRepository {

}
