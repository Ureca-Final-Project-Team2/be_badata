package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

}
