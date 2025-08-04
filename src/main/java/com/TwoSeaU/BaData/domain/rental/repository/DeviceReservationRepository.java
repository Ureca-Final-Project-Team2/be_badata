package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.dto.projection.AvailableDeviceProjection;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
        sd.count AS totalCount,
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

    @Query("""
    SELECT   
        sd.id AS storeDeviceId,
        sd.device.id AS deviceId,
        sd.dataCapacity AS dataCapacity,
        MAX(sd.device.name) AS deviceName,
        sd.price AS price,
        MAX(sd.device.imageUrl) AS imageUrl,
        sd.count AS totalCount,
        sd.count AS availableCount
    FROM StoreDevice sd
    WHERE sd.store.id = :storeId
    GROUP BY sd.id
    """)
    List<AvailableDeviceProjection> findAvailableDevicesByStoreId(
            @Param("storeId") Long storeId
    );



    @Query("""
    SELECT 
        (sd.count - COALESCE(SUM(
            CASE 
                WHEN r.id IS NOT NULL 
                     AND r.rentalEndDate >= :rentalStartDate
                     AND r.rentalStartDate <= :rentalEndDate
                THEN dr.reservationCount
            END
        ), 0)) AS availableCount
    FROM StoreDevice sd
    LEFT JOIN DeviceReservation dr ON dr.storeDevice = sd
    LEFT JOIN Reservation r ON dr.reservation = r
    WHERE sd.id = :storeDeviceId
    GROUP BY sd.count
    """)
    Optional<Long> findAvailableCountsByStoreDeviceIdAndPeriod(
            @Param("storeDeviceId") Long storeDeviceId,
            @Param("rentalStartDate") LocalDateTime rentalStartDate,
            @Param("rentalEndDate") LocalDateTime rentalEndDate
    );

    @Modifying
    @Query("DELETE FROM DeviceReservation dr WHERE dr.reservation.id = :reservationId")
    void deleteByReservationId(@Param("reservationId") Long reservationId);

    @Query("select dr from DeviceReservation dr join fetch dr.storeDevice sd join fetch sd.device where dr.reservation.id = :reservationId")
    List<DeviceReservation> findByReservationIdWithFetchStoreDeviceAndDevice(final Long reservationId);

}
