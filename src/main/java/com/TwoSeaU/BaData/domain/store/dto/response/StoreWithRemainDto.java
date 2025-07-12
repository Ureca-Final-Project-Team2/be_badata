package com.TwoSeaU.BaData.domain.store.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class StoreWithRemainDto {

    private Store store;
    private Double distance;
    private Integer remainingCount;

}
