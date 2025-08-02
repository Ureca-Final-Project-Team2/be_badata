package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.user.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPurchaseGifticonDetailResponse {

    private Long sellerId;
    private String nickname;
    private Long id;
    private String title;
    private String comment;
    private BigDecimal price;
    private LocalDate deadLine;
    private LocalDateTime boughtAt;
    private String partner;

    public static GetPurchaseGifticonDetailResponse from(final User seller,
                                                         final Gifticon gifticon,
                                                         final LocalDateTime boughtAt) {
        return GetPurchaseGifticonDetailResponse.builder()
                .sellerId(seller.getId())
                .nickname(seller.getNickName())
                .id(gifticon.getId())
                .title(gifticon.getTitle())
                .comment(gifticon.getComment())
                .price(gifticon.getPrice())
                .deadLine(gifticon.getDeadLine())
                .boughtAt(boughtAt)
                .partner(gifticon.getPartner())
                .build();
    }
}
