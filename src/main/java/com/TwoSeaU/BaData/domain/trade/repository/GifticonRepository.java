package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GifticonRepository extends JpaRepository<Gifticon,Long> {

    List<Gifticon> findByIsSold(Boolean isSold);
    List<Gifticon> findBySellerId(Long sellerId);
    List<Gifticon> findByDeadLineBefore(LocalDateTime time);
    List<Gifticon> findByTitleContaining(String query);
}
