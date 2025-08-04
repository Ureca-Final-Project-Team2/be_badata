package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.projection.StoreWithDistanceProjection;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface StoreRepository extends JpaRepository<Store,Long> {
    @Query(value = """
    SELECT s.id AS storeId,
           s.name AS name,
           s.store_image AS imageUrl,
           s.detail_address AS detailAddress,
           s.phone_number AS phoneNumber,
           ST_Distance(s.position::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distanceFromMe,
           s.review_rating AS reviewRating,
           s.start_time AS startTime,
           s.end_time AS endTime
    FROM store s
    WHERE s.id = :storeId
    """, nativeQuery = true)
    StoreWithDistanceProjection findStoreWithDistance(
            @Param("storeId") Long storeId,
            @Param("lat") double latitude,
            @Param("lon") double longitude
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Store s WHERE s.id = :id")
    Optional<Store> findByIdWithLock(@Param("id") Long id);

}
