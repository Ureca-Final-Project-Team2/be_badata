package com.TwoSeaU.BaData.domain.store.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class StoreDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int price; // 1일 대여 가격

    @Column(nullable = false)
    private int dataCapacity; // 1일 데이터 한도 => 무제한은 999로 표기

    public static StoreDevice of(final Store store, final Device device, final int count, final int price, final int dataCapacity){

        return StoreDevice.builder()
                .store(store)
                .device(device)
                .count(count)
                .price(price)
                .dataCapacity(dataCapacity)
                .build();
    }
}
