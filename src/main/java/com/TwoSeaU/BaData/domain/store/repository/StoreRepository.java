package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.dto.StoreWithDistanceProjection;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface StoreRepository extends JpaRepository<Store,Long> {

    @Query(value = """
    SELECT s.id AS id, s.name AS name,
           ST_Y(s.position) AS latitude,
           ST_X(s.position) AS longitude,
           ST_Distance(s.position::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distance
    FROM store s
    WHERE ST_DWithin(s.position, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radius)
    ORDER BY distance
    """, nativeQuery = true)
    List<StoreWithDistanceProjection> findStoresWithDistance(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radius") double radius
    );
}
