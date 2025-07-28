package com.TwoSeaU.BaData.domain.store.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Getter
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer dataMaxSpeed;//Mbps 기준

    private Integer supportDevicesCount;

    @Column(nullable = false)
    private Boolean is5G;

    private String imageUrl;

    public static Device of(final String name, final Integer dataMaxSpeed, final Integer supportDevicesCount,
                             final Boolean is5G, final String imageUrl){

        return Device.builder()
                .name(name)
                .dataMaxSpeed(dataMaxSpeed)
                .supportDevicesCount(supportDevicesCount)
                .is5G(is5G)
                .imageUrl(imageUrl)
                .build();
    }
}
