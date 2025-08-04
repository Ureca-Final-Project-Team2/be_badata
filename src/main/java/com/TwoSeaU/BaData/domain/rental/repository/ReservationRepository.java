package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.enums.ReservationStatus;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation,Long>, ReservationQueryRepository {

    @Query("select count(*) from Reservation r where r.store=:store and r.user=:user")
    int countByReservationAndStore(final Store store, final User user);

    @Query("""
          SELECT r FROM Reservation r
          WHERE r.rentalEndDate < :now
          AND r.status !=:reservationStatus
          """)
    List<Reservation> findExpiredReservationsAndNotComplete(@Param("now") final LocalDateTime now,
                                              @Param("reservationStatus") final ReservationStatus reservationStatus);

    @Query("""
          SELECT r FROM Reservation r
          WHERE r.rentalEndDate >= :now AND r.rentalStartDate <= :now
          AND r.status =:reservationStatus
          """)
    List<Reservation> findBurrowingRentalAndPending(@Param("now") final LocalDateTime now,
                                          @Param("reservationStatus") final ReservationStatus reservationStatus);

}
