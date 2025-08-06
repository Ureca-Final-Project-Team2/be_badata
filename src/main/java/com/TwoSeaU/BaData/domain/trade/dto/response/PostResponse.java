package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long id;
    private String title;
    private String partner;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private PostCategory postCategory;
    private String gifticonCategory;
    private LocalDate deadLine;
    private MobileCarrier mobileCarrier;
    private Integer likesCount;
    private Boolean isLiked;
    private Integer capacity;

    public static PostResponse from(final Post post, final Integer likesCount, final Boolean isLiked) {

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
                .price(post.getPrice())
                .createdAt(post.getCreatedAt())
                .postCategory(post instanceof Gifticon ? PostCategory.GIFTICON : PostCategory.DATA)
                .gifticonCategory(post instanceof Gifticon gifticon ? gifticon.getCategory().getCategoryName() : null)
                .deadLine(post.getDeadLine())
                .mobileCarrier(post instanceof Data data ? data.getMobileCarrier() : null)
                .likesCount(likesCount)
                .isLiked(isLiked)
                .capacity(post instanceof Data data ? data.getCapacity() : null)
                .build();
    }
}
