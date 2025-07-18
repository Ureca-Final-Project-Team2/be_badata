package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}
