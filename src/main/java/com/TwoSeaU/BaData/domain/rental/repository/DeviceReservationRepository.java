package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.dto.projection.AvailableDeviceProjection;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceReservationRepository extends JpaRepository<DeviceReservation,Long> {

    @Query("""
    SELECT   
        sd.id AS storeDeviceId,
        sd.device.id AS deviceId,
        sd.dataCapacity AS dataCapacity,
        MAX(sd.device.name) AS deviceName,
        sd.price AS price,
        MAX(sd.device.imageUrl) AS imageUrl,
        (sd.count - COALESCE(SUM(
            CASE 
                WHEN r.id IS NOT NULL 
                     AND r.rentalEndDate >= :rentalStartDate
                     AND r.rentalStartDate <= :rentalEndDate
                THEN dr.reservationCount
                ELSE 0
            END
        ), 0)) AS availableCount
    FROM StoreDevice sd
    LEFT JOIN DeviceReservation dr ON dr.storeDevice = sd
    LEFT JOIN Reservation r ON dr.reservation = r
    WHERE sd.store.id = :storeId
    GROUP BY sd.id
    """)
    List<AvailableDeviceProjection> findAvailableDevicesByStoreIdAndPeriod(
            @Param("storeId") Long storeId,
            @Param("rentalStartDate") LocalDateTime rentalStartDate,
            @Param("rentalEndDate") LocalDateTime rentalEndDate
    );

}
