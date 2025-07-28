package com.TwoSeaU.BaData.domain.user.entity;

import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "address")
@Getter
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String kaKaoAddressId;

    private String phone;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String roadAddressName;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public static Address of(final String addressName,
                             final String kaKaoAddressId,
                             final String phone,
                             final String placeName,
                             final String roadAddressName,
                             final Point location,
                             final User user){

        return Address.builder()
                .addressName(addressName)
                .kaKaoAddressId(kaKaoAddressId)
                .phone(phone)
                .placeName(placeName)
                .roadAddressName(roadAddressName)
                .location(location)
                .user(user)
                .build();
    }
}
