package com.TwoSeaU.BaData.domain.store.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Getter
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point position;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer availableDevice;

    @Column(nullable = false)
    private String detailAddress;

    private String storeImage;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    public static Store of(final String name, final Point position,final String phoneNumber, final String detailAddress,
                            final Integer availableDevice, final String storeImage, final LocalTime startTime, final LocalTime endTime){

        return Store.builder()
                .name(name)
                .position(position)
                .phoneNumber(phoneNumber)
                .availableDevice(availableDevice)
                .detailAddress(detailAddress)
                .storeImage(storeImage)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

}
