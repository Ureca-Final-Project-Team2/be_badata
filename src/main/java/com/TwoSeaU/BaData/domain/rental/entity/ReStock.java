package com.TwoSeaU.BaData.domain.rental.entity;

import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import com.TwoSeaU.BaData.global.response.GeneralException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ReStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_device_id", nullable = false)
    private StoreDevice storeDevice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer desiredCount;

    @Column(nullable = false)
    private LocalDateTime desiredStartDate;

    @Column(nullable = false)
    private LocalDateTime desiredEndDate;

    public static ReStock of(final StoreDevice storeDevice, final User user,
                             final LocalDateTime desiredStartDate, final LocalDateTime desiredEndDate, final Integer desiredCount){

        if (desiredStartDate.isAfter(desiredEndDate)) {
            throw new GeneralException(RentalException.CANT_END_DATE_BEFORE_THAN_START_DATE);
        }

        return ReStock.builder()
                .user(user)
                .storeDevice(storeDevice)
                .desiredStartDate(desiredStartDate)
                .desiredEndDate(desiredEndDate)
                .desiredCount(desiredCount)
                .build();
    }
}
