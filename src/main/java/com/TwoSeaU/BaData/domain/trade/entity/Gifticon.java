package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "gifticon")
public class Gifticon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String associateCompany;

    private LocalDateTime expirationDate;

    private LocalDateTime issueDate;

    private String couponNumber;

    private String partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private GifticonCategory category;

    public static Gifticon of(final Post post, final String associateCompany,
                              final LocalDateTime expirationDate, final LocalDateTime issueDate,
                              final String couponNumber, final String partner,
                              final GifticonCategory category) {

        return Gifticon.builder()
                .post(post)
                .associateCompany(associateCompany)
                .expirationDate(expirationDate)
                .issueDate(issueDate)
                .couponNumber(couponNumber)
                .partner(partner)
                .category(category)
                .build();
    }
}