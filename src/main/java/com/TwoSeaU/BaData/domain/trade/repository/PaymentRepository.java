package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TwoSeaU.BaData.domain.trade.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findAllByUserId(Long userId);
}
