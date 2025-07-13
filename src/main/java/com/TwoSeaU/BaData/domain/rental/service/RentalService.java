package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservationDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final DeviceReservationRepository deviceReservationRepository;
    private final StoreRepository storeRepository;

    public List<ShowReservationDeviceInfoResponse> getReservationDeviceInfoResponse(final LocalDateTime rentalStartDate,
                                                                                    final LocalDateTime rentalEndDate,
                                                                                    final Long storeId){

        if(!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        return deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(storeId,rentalStartDate,rentalEndDate)
                .stream().map(ShowReservationDeviceInfoResponse::from).toList();
    }

}
