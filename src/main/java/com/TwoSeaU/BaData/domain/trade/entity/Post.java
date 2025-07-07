package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "post_type")
@Table(name = "post")
public abstract class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    private String title;

    private String comment;

    private Integer price;

    private LocalDateTime deadLine;

    private String postImage;

    private Boolean isSold;

    public static Post of(final User seller, final String title, final String comment,
                          final Integer price, final LocalDateTime deadLine,
                          final String postImage, final Boolean isSold) {

        return Post.builder()
                .seller(seller)
                .title(title)
                .comment(comment)
                .price(price)
                .deadLine(deadLine)
                .postImage(postImage)
                .isSold(isSold)
                .build();
    }
}
