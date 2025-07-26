package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveDeviceRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveRentalRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowRentalResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservationDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservedDeviceResponse;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final UserRepository userRepository;
    private final StoreDeviceRepository storeDeviceRepository;
    private final ReservationRepository reservationRepository;
    private final RestockNotificationService restockNotificationService;
    private final EntityManager em;



    public List<ShowReservationDeviceInfoResponse> getReservationDeviceInfoResponse(final LocalDateTime rentalStartDate,
                                                                                    final LocalDateTime rentalEndDate,
                                                                                    final Long storeId){

        if (!storeRepository.existsById(storeId)){
            throw new GeneralException(StoreException.CANT_FIND_STORE);
        }

        if (rentalStartDate == null || rentalEndDate == null){
            return deviceReservationRepository.findAvailableDevicesByStoreId(storeId).stream()
                    .map(ShowReservationDeviceInfoResponse::from).toList();
        }

        return deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(storeId,rentalStartDate,rentalEndDate)
                .stream().map(ShowReservationDeviceInfoResponse::from).toList();
    }

    @Transactional
    public Long reserveRental(final ReserveRentalRequest reserveRentalRequest,final String username){

        validateRentalCondition(reserveRentalRequest);

        final User user = userRepository.findByUsername(username).orElseThrow(()->new GeneralException(
                UserException.COIN_NOT_FOUND));

        final Store store = storeRepository.findById(reserveRentalRequest.getStoreId()).orElseThrow(()-> new GeneralException(
                StoreException.CANT_FIND_STORE));

        final Reservation reservation = Reservation.of(user,store,reserveRentalRequest.getRentalStartDate(),
                reserveRentalRequest.getRentalEndDate());

        reservationRepository.save(reservation);

        int totalPrice = 0;
        final long rentalDays = ChronoUnit.DAYS.between(
                reserveRentalRequest.getRentalStartDate().toLocalDate(),
                reserveRentalRequest.getRentalEndDate().toLocalDate()
        );
        long adjustedRentalDays = (rentalDays == 0) ? 1 : rentalDays;

        for (final ReserveDeviceRequest reserveDeviceRequest : reserveRentalRequest.getStoreDevices()) {

            final StoreDevice storeDevice = storeDeviceRepository.findById(reserveDeviceRequest.getStoreDeviceId())
                    .orElseThrow(() -> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

            deviceReservationRepository.save(DeviceReservation.of(reservation, storeDevice,
                    reserveDeviceRequest.getCount()));

            totalPrice += (storeDevice.getPrice() * reserveDeviceRequest.getCount() * (int) adjustedRentalDays);
        }

        reservation.changePrice(totalPrice);

        return reservation.getId();
    }
    public ShowRentalResponse getReservedDeviceByReservationId(final Long reservationId, final String username){

        // 예약 조회
        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()-> new GeneralException(RentalException.RESERVATION_NOT_FOUND));

        // 로그인한 유저 조회
        final User loginUser = userRepository.findByUsername(username).orElseThrow(()-> new GeneralException(UserException.USER_NOT_FOUND));

        if(!reservation.getUser().getId().equals(loginUser.getId())){
            throw new GeneralException(RentalException.CANT_ACCESS_TO_OTHER_RESERVATION);
        }

        // 특정 예약에 대한 장치 가져오기
        final List<ShowReservedDeviceResponse> reservedStoreDevice = deviceReservationRepository.findByReservationIdWithFetchStoreDeviceAndDevice(reservationId).stream().map(deviceReservation -> {
            final StoreDevice storeDevice = deviceReservation.getStoreDevice();
            return ShowReservedDeviceResponse.from(storeDevice, deviceReservation, reservation);
        }).toList();

        return ShowRentalResponse.of(reservation.getStore().getName(), reservedStoreDevice);

    }

    @Transactional
    public Long deleteReserveRental(final Long reservationId, final String username){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()-> new GeneralException(UserException.USER_NOT_FOUND));

        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()->new GeneralException(RentalException.RESERVATION_NOT_FOUND));

        validateCancelReservation(loginUser, reservation);

        final List<DeviceReservation> deviceReservations = deviceReservationRepository.findByReservationIdWithFetchStoreDeviceAndDevice(reservationId);

        deviceReservationRepository.deleteByReservationId(reservationId);
        reservationRepository.delete(reservation);

        em.flush();

        restockNotificationService.sendRestockNotification(reservation,deviceReservations);

        return reservation.getId();
    }



    private void validateCancelReservation(final User loginUser, final Reservation reservation) {

        if(!reservation.getUser().getId().equals(loginUser.getId())){

            throw new GeneralException(RentalException.CANT_CANCEL_RESERVED_USERS);
        }

        if(reservation.getStatus().equals(ReservationStatus.BURROWING) ||
                reservation.getStatus().equals(ReservationStatus.COMPLETE)){

            throw new GeneralException(RentalException.CANT_CANCEL_ALREADY_RENTAL);
        }
    }

    private void validateRentalCondition(final ReserveRentalRequest reserveRentalRequest){

        reserveRentalRequest.getStoreDevices().forEach(reserveDeviceRequest -> {

            final StoreDevice storeDevice = storeDeviceRepository.findById(reserveDeviceRequest.getStoreDeviceId())
                    .orElseThrow(()-> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

            if(!storeDevice.getStore().getId().equals(reserveRentalRequest.getStoreId())){
                throw new GeneralException(RentalException.DONT_MATCH_STORE_DEVICE_STORE);
            }

            if(storeDevice.getCount() < reserveDeviceRequest.getCount()){
                throw new GeneralException(RentalException.CANT_RESERVATION_MORE_THAN_COUNT);
            }

            Long availableCount = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    reserveDeviceRequest.getStoreDeviceId(),
                    reserveRentalRequest.getRentalStartDate(),
                    reserveRentalRequest.getRentalEndDate()).orElseThrow(()-> new GeneralException(StoreException.CANT_FIND_STORE_DEVICE));

            if(availableCount<reserveDeviceRequest.getCount()){
                throw new GeneralException(RentalException.ALREADY_RENTAL_EXIST_SAME_PERIOD);
            }

        });
    }

}
