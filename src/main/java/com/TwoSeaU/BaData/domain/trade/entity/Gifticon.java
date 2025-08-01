package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@DiscriminatorValue("GIFTICON")
@Table(name = "gifticon")
public class Gifticon extends Post{

    @Column(nullable = false)
    private String couponNumber;

    @Column(nullable = false)
    private String partner;

    private double[] vector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private GifticonCategory category;

    private LocalDateTime barcodeViewTime;

    public Gifticon(final User user, final String title, final String comment, final BigDecimal price,
                    final LocalDate deadLine, final String postImage, final Boolean isSold,
                    final String couponNumber, final String partner, final GifticonCategory category,
                    final double[] vector) {
        super(user, title, comment, price, deadLine, postImage, isSold);
        this.couponNumber = couponNumber;
        this.partner = partner;
        this.category = category;
        this.vector = vector;
    }

    public void updateVector(double[] vector) {
        this.vector = vector;
    }

    public void updateBarcodeViewTime(LocalDateTime barcodeViewTime) {
        this.barcodeViewTime = barcodeViewTime;
    }
}