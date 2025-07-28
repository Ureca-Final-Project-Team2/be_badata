package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowRentalResponse {

    private Long storeId;

    private String storeName;

    private List<ShowReservedDeviceResponse> showReservedDeviceResponses = new ArrayList<>();

    private Integer countOfVisit;

    public static ShowRentalResponse of(final Store store, final List<ShowReservedDeviceResponse> showReservedDeviceResponses, final Integer countOfVisit){

        return ShowRentalResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .showReservedDeviceResponses(showReservedDeviceResponses)
                .countOfVisit(countOfVisit)
                .build();
    }

}
