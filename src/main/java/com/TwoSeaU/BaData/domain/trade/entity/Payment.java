package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.trade.enums.PayMethod;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String merchantUid;

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public static Payment of(User user, Post post, String merchantUid, PayMethod payMethod, BigDecimal amount) {
        return Payment.builder()
                .user(user)
                .post(post)
                .merchantUid(merchantUid)
                .payMethod(payMethod)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
