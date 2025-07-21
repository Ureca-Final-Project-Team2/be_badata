package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation,Long>, ReservationQueryRepository {

    @Query("select count(*) from Reservation r where r.store=:store and r.user=:user")
    int countByReservationAndStore(final Store store, final User user);

}
