package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@DiscriminatorValue("GIFTICON")
@Table(name = "gifticon")
public class Gifticon extends Post{
    private LocalDateTime issueDate;

    private String couponNumber;

    private String partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private GifticonCategory category;

    public static Gifticon of(final LocalDateTime issueDate, final String couponNumber,
                              final String partner, final GifticonCategory category) {

        return Gifticon.builder()
                .issueDate(issueDate)
                .couponNumber(couponNumber)
                .partner(partner)
                .category(category)
                .build();
    }
}