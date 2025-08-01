package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReStockRepository;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.repository.FcmTokenRepository;
import com.TwoSeaU.BaData.global.fcm.FCMService;
import com.TwoSeaU.BaData.global.fcm.dto.NotificationRequest;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestockNotificationService {

    private final DeviceReservationRepository deviceReservationRepository;
    private final ReStockRepository reStockRepository;
    private final FCMService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    private static final String restockTitle = "찜한 와이파이 왔어요!";
    private static final String restockContent = " 지금 아니면 또 놓칠지도 몰라요, 파도처럼 \uD83C\uDF0A";


    public void sendRestockNotification(final Reservation reservation, final List<DeviceReservation> deviceReservations){

        final List<User> sendTargetUser = getSendTargetUser(reservation, deviceReservations);

        final List<String> fcmTokens = fcmTokenRepository.findByUserIn(sendTargetUser)
                .stream().map(fcmToken -> fcmToken.getToken()).toList();

        if(fcmTokens.isEmpty()){
            return;
        }

        fcmService.sendToManyUser(NotificationRequest.forMultipleTokens(restockTitle, restockContent, fcmTokens,
                Map.of("storeId", String.valueOf(reservation.getStore().getId()))
                ));

    }

    private List<User> getSendTargetUser(final Reservation reservation, final List<DeviceReservation> deviceReservations) {

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

                log.debug("AvailableCount: {}, Restock DesiredCount: {}", availableCount, reStock.getDesiredCount());


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
