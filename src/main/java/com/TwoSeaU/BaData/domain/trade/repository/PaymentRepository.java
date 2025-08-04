package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentQueryRepository {

	Optional<Payment> findByMerchantUid(final String merchantUid);
	int countByUserIdAndPaymentStatus(final Long buyerId, final PaymentStatus paymentStatus);
	Optional<Payment> findByUserIdAndPostIdAndPaymentStatus(final Long buyerId, final Long postId, final PaymentStatus paymentStatus);

	@Query("SELECT COUNT(p) FROM Payment p "
		+ "WHERE p.user.id = :buyerId "
		+ "AND p.post.isDeleted = false")
	int countByUserIdAndPostIsNotDeleted(@Param("buyerId") final Long buyerId);
}
