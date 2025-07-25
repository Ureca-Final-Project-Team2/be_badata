package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@DiscriminatorValue("GIFTICON")
@Table(name = "gifticon")
public class Gifticon extends Post{

    private String couponNumber;

    private String partner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private GifticonCategory category;

    public Gifticon(final User user, final String title, final String comment, final Integer price,
                    final LocalDate deadLine, final String postImage, final Boolean isSold,
                    final String couponNumber, final String partner, final GifticonCategory category) {
        super(user, title, comment, price, deadLine, postImage, isSold);
        this.couponNumber = couponNumber;
        this.partner = partner;
        this.category = category;
    }
}