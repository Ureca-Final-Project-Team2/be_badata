package com.TwoSeaU.BaData.domain.store.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreSearchRequest {

    private boolean isOpeningNow;
    private double swLat;
    private double swLng;
    private double neLat;
    private double neLng;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    private double reviewRating;
    private double minPrice;
    private double maxPrice;
    private List<Integer> dataCapacity;
    private boolean is5G;
    private List<Integer> maxSupportConnection;




}
