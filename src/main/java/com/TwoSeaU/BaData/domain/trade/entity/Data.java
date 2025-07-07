package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@DiscriminatorValue("data")
@Table(name = "data")
public class Data extends Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MobileCarrier mobileCarrier;

    private Integer capacity;

    public static Data of(final MobileCarrier mobileCarrier, final Integer capacity) {

        return Data.builder()
                .mobileCarrier(mobileCarrier)
                .capacity(capacity)
                .build();
    }
}
