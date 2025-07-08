package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    public static Data of(final MobileCarrier mobileCarrier, final Integer capacity) {

        return Data.builder()
                .mobileCarrier(mobileCarrier)
                .capacity(capacity)
                .build();
    }
}
