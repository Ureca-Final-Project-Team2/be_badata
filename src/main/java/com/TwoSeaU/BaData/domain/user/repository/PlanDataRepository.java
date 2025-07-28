package com.TwoSeaU.BaData.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TwoSeaU.BaData.domain.user.entity.PlanData;

public interface PlanDataRepository extends JpaRepository<PlanData, Long> {
}
