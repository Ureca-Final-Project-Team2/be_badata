package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.RestockDeviceRequest;
import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReStockRepository;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestockService {

    private final ReStockRepository reStockRepository;
    private final StoreDeviceRepository storeDeviceRepository;
    private final DeviceReservationRepository deviceReservationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long restockStoreDevice(final RestockDeviceRequest restockDeviceRequest, final String username){

        final StoreDevice storeDevice = storeDeviceRepository.findById(restockDeviceRequest.getStoreDeviceId())
                .orElseThrow(()-> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

        validateRestock(restockDeviceRequest, storeDevice);

        final User user = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.USER_NOT_FOUND));

        final ReStock reStock = reStockRepository.save(
                ReStock.of(storeDevice, user, restockDeviceRequest.getDesiredStartDate(), restockDeviceRequest.getDesiredEndDate()));

        return reStock.getId();
    }


    // 해당 기간에 예약 가능하다면 예외 처리
    private void validateRestock(final RestockDeviceRequest restockDeviceRequest, final StoreDevice storeDevice){

        // 재입고 알림 요청 댓수가 가맹점이 소유한 기기보다 더 많다면
        if(storeDevice.getCount()<restockDeviceRequest.getCount()){
            throw new GeneralException(RentalException.CANT_RESTOCK_MORE_THAN_COUNT);
        }

        final Long availableCount = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                restockDeviceRequest.getStoreDeviceId(),
                restockDeviceRequest.getDesiredStartDate(),
                restockDeviceRequest.getDesiredEndDate()).orElseThrow(()-> new GeneralException(
                StoreException.CANT_FIND_STORE_DEVICE));

        if(availableCount >= restockDeviceRequest.getCount()){
            throw new GeneralException(RentalException.CANT_RESTOCK_WHEN_AVAILABLE_COUNT);
        }

    }

}
