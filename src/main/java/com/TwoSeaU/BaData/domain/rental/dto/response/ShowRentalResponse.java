package com.TwoSeaU.BaData.domain.rental.dto.response;

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

    private String storeName;

    private List<ShowReservedDeviceResponse> showReservedDeviceResponses = new ArrayList<>();

    private Integer countOfVisit;

    public static ShowRentalResponse of(final String storeName, final List<ShowReservedDeviceResponse> showReservedDeviceResponses, final Integer countOfVisit){

        return ShowRentalResponse.builder()
                .storeName(storeName)
                .showReservedDeviceResponses(showReservedDeviceResponses)
                .countOfVisit(countOfVisit)
                .build();
    }

}
