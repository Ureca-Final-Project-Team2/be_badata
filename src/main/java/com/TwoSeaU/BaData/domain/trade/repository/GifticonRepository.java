package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GifticonRepository extends JpaRepository<Gifticon,Long>, GifticonQueryRepository {
    Boolean existsByCouponNumber(final String couponNumber);
}
