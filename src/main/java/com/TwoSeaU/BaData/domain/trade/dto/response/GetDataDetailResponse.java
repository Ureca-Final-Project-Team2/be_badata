package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetDataDetailResponse {
    private Long id;
    private String title;
    private String comment;
    private BigDecimal price;
    private LocalDate deadLine;
    private String postImage;
    private Boolean isSold;
    private LocalDateTime createdAt;
    private MobileCarrier mobileCarrier;
    private Integer capacity;
    private Integer likesCount;
    private Boolean isLiked;

    public static GetDataDetailResponse from(final Data data, final Integer likesCount, final Boolean isLiked) {
        return GetDataDetailResponse.builder()
                .id(data.getId())
                .title(data.getTitle())
                .comment(data.getComment())
                .price(data.getPrice())
                .deadLine(data.getDeadLine())
                .postImage(data.getPostImage())
                .isSold(data.getIsSold())
                .createdAt(data.getCreatedAt())
                .mobileCarrier(data.getMobileCarrier())
                .capacity(data.getCapacity())
                .likesCount(likesCount)
                .isLiked(isLiked)
                .build();
    }
}
