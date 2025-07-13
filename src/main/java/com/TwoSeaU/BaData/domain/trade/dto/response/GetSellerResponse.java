package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.user.entity.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetSellerResponse {
    private Long userId;
    private String username;

    public static GetSellerResponse from(final User user) {
        return GetSellerResponse.builder()
                .userId(user.getId())
                .username(user.getNickName())
                .build();
    }
}
