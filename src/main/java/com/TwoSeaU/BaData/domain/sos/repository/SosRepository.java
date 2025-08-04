package com.TwoSeaU.BaData.domain.sos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.TwoSeaU.BaData.domain.sos.entity.Sos;

import jakarta.persistence.LockModeType;

public interface SosRepository extends JpaRepository<Sos, Long>, SosQueryRepository {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Sos s WHERE s.id = :id")
	Optional<Sos> findByIdForUpdate(@Param("id") Long id);
}
