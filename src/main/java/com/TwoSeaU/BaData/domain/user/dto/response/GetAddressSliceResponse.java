package com.TwoSeaU.BaData.domain.user.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAddressSliceResponse {

    private List<GetAddressResponse> getAddressResponses = new ArrayList<>();
    private boolean hasNext;

    public static GetAddressSliceResponse of(final List<GetAddressResponse> getAddressResponses, final boolean hasNext){

        return GetAddressSliceResponse.builder()
                .getAddressResponses(getAddressResponses)
                .hasNext(hasNext)
                .build();
    }

}
