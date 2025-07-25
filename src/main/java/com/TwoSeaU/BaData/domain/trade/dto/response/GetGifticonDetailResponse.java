package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetGifticonDetailResponse {
    private Long id;
    private String title;
    private String comment;
    private Integer price;
    private LocalDate deadLine;
    private String postImage;
    private Boolean isSold;
    private LocalDateTime createdAt;
    private String partner;
    private Integer likesCount;
    private Boolean isLiked;

    public static GetGifticonDetailResponse from(final Gifticon gifticon, final Integer likesCount, final Boolean isLiked) {
        return GetGifticonDetailResponse.builder()
                .id(gifticon.getId())
                .title(gifticon.getTitle())
                .comment(gifticon.getComment())
                .price(gifticon.getPrice())
                .deadLine(gifticon.getDeadLine())
                .postImage(gifticon.getPostImage())
                .isSold(gifticon.getIsSold())
                .createdAt(gifticon.getCreatedAt())
                .partner(gifticon.getPartner())
                .likesCount(likesCount)
                .isLiked(isLiked)
                .build();
    }
}
