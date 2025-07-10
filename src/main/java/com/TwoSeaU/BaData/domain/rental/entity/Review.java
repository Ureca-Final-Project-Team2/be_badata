package com.TwoSeaU.BaData.domain.rental.entity;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private String content;

    private Integer rating;

    private String imageUrl;

    public static Review of(final Store store, final Reservation reservation, final String content, final Integer rating,
                            final String imageUrl){

        return Review.builder()
                .store(store)
                .reservation(reservation)
                .content(content)
                .rating(rating)
                .imageUrl(imageUrl)
                .build();
    }

}
