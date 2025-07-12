package com.TwoSeaU.BaData.domain.store.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreSearchRequest {

    private final Double centerLat;
    private final Double centerLng;
    private final Boolean isOpeningNow;
    private final LocalDateTime rentalStartDate;
    private final LocalDateTime rentalEndDate;
    private final Double reviewRating;
    private final Integer minPrice;
    private final Integer maxPrice;
    private final List<Integer> dataCapacity;
    private final Boolean is5G;
    private final List<Integer> maxSupportConnection;

}
