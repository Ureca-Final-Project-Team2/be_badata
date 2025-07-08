package com.TwoSeaU.BaData.domain.trade.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
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

    public static PostResponse from(final Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .createdAt(post.getCreatedAt())
                .postImage(post.getPostImage())
                .postCategory(post instanceof Gifticon ? PostCategory.GIFTICON : PostCategory.DATA)
                .partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
                .build();
    }
}
