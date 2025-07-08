package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DataRepository extends JpaRepository<Data, Long> {

    List<Data> findByIsSold(Boolean isSold);
    List<Data> findBySellerId(Long sellerId);
    List<Data> findByDeadLineBefore(LocalDateTime time);
    List<Data> findByTitleContaining(String query);
}
