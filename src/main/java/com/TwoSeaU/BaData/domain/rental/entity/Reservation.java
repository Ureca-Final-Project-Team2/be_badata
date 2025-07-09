package com.TwoSeaU.BaData.domain.rental.entity;

import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime rentalStartDate;

    @Column(nullable = false)
    private LocalDateTime rentalEndDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public static Reservation of(final User user, final LocalDateTime rentalStartDate, final LocalDateTime rentalEndDate){

        return Reservation.builder()
                .user(user)
                .rentalStartDate(rentalStartDate)
                .rentalEndDate(rentalEndDate)
                .status(ReservationStatus.PENDING)
                .build();
    }



}
