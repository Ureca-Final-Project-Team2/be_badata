package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;
import java.util.Optional;

import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TwoSeaU.BaData.domain.trade.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentQueryRepository {

	Optional<Payment> findByMerchantUid(final String merchantUid);
	Optional<Payment> findByUserIdAndPostIdAndPaymentStatus(final Long buyerId, final Long postId, final PaymentStatus paymentStatus);
	int countByUserId(final Long buyerId);
}
