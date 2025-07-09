package com.TwoSeaU.BaData.domain.rental.repository;

import com.TwoSeaU.BaData.domain.rental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {

}
