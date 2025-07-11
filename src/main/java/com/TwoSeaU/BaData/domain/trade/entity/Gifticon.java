package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
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

    public Gifticon(User user, String title, String comment, Integer price,
                LocalDateTime deadLine, String postImage, Boolean isSold,
                    LocalDateTime issueDate, String couponNumber, String partner, GifticonCategory category) {
        super(user, title, comment, price, deadLine, postImage, isSold);
        this.issueDate = issueDate;
        this.couponNumber = couponNumber;
        this.partner = partner;
        this.category = category;
    }
}