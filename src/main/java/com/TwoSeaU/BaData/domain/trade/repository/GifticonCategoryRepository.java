package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GifticonCategoryRepository extends JpaRepository<GifticonCategory, Long> {
    Optional<GifticonCategory> findByCategoryName(String categoryName);
}
