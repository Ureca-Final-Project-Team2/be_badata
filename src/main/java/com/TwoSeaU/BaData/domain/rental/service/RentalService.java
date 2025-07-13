package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveRentalRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservationDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final DeviceReservationRepository deviceReservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreDeviceRepository storeDeviceRepository;
    private final ReservationRepository reservationRepository;

    public List<ShowReservationDeviceInfoResponse> getReservationDeviceInfoResponse(final LocalDateTime rentalStartDate,
                                                                                    final LocalDateTime rentalEndDate,
                                                                                    final Long storeId){

        if(!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        return deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(storeId,rentalStartDate,rentalEndDate)
                .stream().map(ShowReservationDeviceInfoResponse::from).toList();
    }

    @Transactional
    public Long reserveRental(final ReserveRentalRequest reserveRentalRequest,final String username){

        validateRentalCondition(reserveRentalRequest);

        final User user = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.COIN_NOT_FOUND));

        final Reservation reservation = Reservation.of(user,reserveRentalRequest.getRentalStartDate(),reserveRentalRequest.getRentalEndDate());
        reservationRepository.save(reservation);

        reserveRentalRequest.getStoreDevices().forEach(reserveDeviceRequest -> {

            final StoreDevice storeDevice = storeDeviceRepository.findById(reserveDeviceRequest.getStoreDeviceId())
                            .orElseThrow(()-> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

            deviceReservationRepository.save(DeviceReservation.of(reservation,storeDevice,
                    reserveDeviceRequest.getCount()));
        });

        return reservation.getId();
    }

    private void validateRentalCondition(final ReserveRentalRequest reserveRentalRequest){

        reserveRentalRequest.getStoreDevices().forEach(reserveDeviceRequest -> {

            Long availableCount = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    reserveDeviceRequest.getStoreDeviceId(),
                    reserveRentalRequest.getRentalStartDate(),
                    reserveRentalRequest.getRentalEndDate());

            if(availableCount<reserveDeviceRequest.getCount()){
                throw new GeneralException(RentalException.ALREADY_RENTAL_EXIST_SAME_PERIOD);
            }

        });
    }

}
