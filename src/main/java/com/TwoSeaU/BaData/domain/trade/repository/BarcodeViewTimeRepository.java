package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.BarcodeViewTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarcodeViewTimeRepository extends JpaRepository<BarcodeViewTime, Long> {
    Optional<BarcodeViewTime> findByPaymentId(final Long paymentId);
}
