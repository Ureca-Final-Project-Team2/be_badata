package com.TwoSeaU.BaData.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long>, CoinHistoryQueryRepository {

}
