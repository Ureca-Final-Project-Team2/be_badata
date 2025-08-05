package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReStockRepository;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestockDeleteTargetingService {

    private final ReStockRepository reStockRepository;
    private final DeviceReservationRepository deviceReservationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<User> getSendTargetUser(final Reservation reservation, final List<DeviceReservation> deviceReservations) {

        final List<User> sendUserTargets = new ArrayList<>();

        final LocalDateTime rentalStart = reservation.getRentalStartDate();
        final LocalDateTime rentalEnd = reservation.getRentalEndDate();

        for(final DeviceReservation deviceReservation : deviceReservations){

            final StoreDevice storeDevice = deviceReservation.getStoreDevice();

            // 1. 취소된 예약 기간과 겹치는 알림 신청자만 조회
            final List<ReStock> overlappedReStocks = reStockRepository.findOverlappedReStocks(
                    storeDevice, rentalStart, rentalEnd
            );

            for (final ReStock reStock : overlappedReStocks) {

                // 2. 신청자의 희망 기간 동안의 남은 수량 계산
                final Long availableCount = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(storeDevice.getId(), rentalStart, rentalEnd)
                        .orElseThrow(()-> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

                log.info("AvailableCount: {}, Restock DesiredCount: {}", availableCount, reStock.getDesiredCount());

                // 3. 남은 수량이 재입고 알림 수량 보다 큰 경우
                if (availableCount >= reStock.getDesiredCount()) {
                    sendUserTargets.add(reStock.getUser());
                    reStockRepository.delete(reStock);
                }
            }

        }

        return sendUserTargets;
    }

}
