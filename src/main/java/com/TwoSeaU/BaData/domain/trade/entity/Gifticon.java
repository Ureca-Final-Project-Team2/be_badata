package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
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
    private LocalDateTime issueDate;

    private String couponNumber;

    private String partner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private GifticonCategory category;

    public Gifticon(final User user, String title, final String comment, final Integer price,
                    final LocalDate deadLine, final String postImage, final Boolean isSold,
                    final LocalDateTime issueDate, final String couponNumber, final String partner, final GifticonCategory category) {
        super(user, title, comment, price, deadLine, postImage, isSold);
        this.issueDate = issueDate;
        this.couponNumber = couponNumber;
        this.partner = partner;
        this.category = category;
    }
}