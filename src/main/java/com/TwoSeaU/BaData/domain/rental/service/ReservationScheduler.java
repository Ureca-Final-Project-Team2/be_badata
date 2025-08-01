package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 50 19 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateReservationStatusForToday() {

        final LocalDateTime now = LocalDateTime.now();

        final List<Reservation> burrowingReservation = reservationRepository.findBurrowingRental(now, ReservationStatus.PENDING);

        for(final Reservation reservation : burrowingReservation){
            reservation.updateStatus(ReservationStatus.BURROWING);
        }

        final List<Reservation> todayEndingRentalDate = reservationRepository.findNotExpiredReservations(now, ReservationStatus.COMPLETE);

        for(final Reservation reservation : todayEndingRentalDate){
            reservation.updateStatus(ReservationStatus.COMPLETE);
        }

    }

}
