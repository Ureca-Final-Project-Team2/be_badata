package com.TwoSeaU.BaData.domain.store.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreMapSearchRequest {

    private final Boolean isOpeningNow;
    private final Double swLat;
    private final Double swLng;
    private final Double neLat;
    private final Double neLng;
    private final LocalDateTime rentalStartDate;
    private final LocalDateTime rentalEndDate;
    private final Double reviewRating;
    private final Integer minPrice;
    private final Integer maxPrice;
    private final List<Integer> dataCapacity;
    private final Boolean is5G;
    private final List<Integer> maxSupportConnection;

}
