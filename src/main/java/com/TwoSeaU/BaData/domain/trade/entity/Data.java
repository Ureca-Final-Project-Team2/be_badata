package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import com.TwoSeaU.BaData.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@DiscriminatorValue("DATA")
@Table(name = "data")
public class Data extends Post {

    @Enumerated(EnumType.STRING)
    private MobileCarrier mobileCarrier;

    private Integer capacity;

    public Data(final User user, final String title, final String comment, final Integer price,
                final LocalDate deadLine, final String postImage, final Boolean isSold,
                final MobileCarrier mobileCarrier, final Integer capacity) {
        super(user, title, comment, price, deadLine, postImage, isSold);
        this.mobileCarrier = mobileCarrier;
        this.capacity = capacity;
    }
}
