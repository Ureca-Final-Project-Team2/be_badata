package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "barcode_view_time")
public class BarcodeViewTime extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public static BarcodeViewTime of(final Payment payment) {
        return BarcodeViewTime.builder()
                .payment(payment)
                .build();
    }
}
