package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReStockRepository extends JpaRepository<ReStock,Long>, ReStockQueryRepository {

    @Query("""
           SELECT r FROM ReStock r
           WHERE r.storeDevice = :storeDevice
           AND r.desiredStartDate <= :rentalEndDate
           AND r.desiredEndDate >= :rentalStartDate
           """)
    List<ReStock> findOverlappedReStocks(
            @Param("storeDevice") StoreDevice storeDevice,
            @Param("rentalStartDate") LocalDateTime rentalStartDate,
            @Param("rentalEndDate") LocalDateTime rentalEndDate);
}
