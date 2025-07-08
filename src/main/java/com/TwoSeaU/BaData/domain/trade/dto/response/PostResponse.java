package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.PostCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long id;
    private String title;
    private String partner;
    private Integer price;
    private LocalDateTime createdAt;
    private String postImage;
    private PostCategory postCategory;

    public static PostResponse of(final Long id, final String title,
                        final String partner, final Integer price,
                        final LocalDateTime createdAt, final String postImage, final PostCategory postCategory) {

        return PostResponse.builder()
                .id(id)
                .title(title)
                .partner(partner)
                .price(price)
                .createdAt(createdAt)
                .postImage(postImage)
                .postCategory(postCategory)
                .build();
    }
}
