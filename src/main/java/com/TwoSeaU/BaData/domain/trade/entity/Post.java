package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Post(User seller, String title, String comment, Integer price, LocalDateTime deadLine, String postImage, Boolean isSold) {
        this.seller = seller;
        this.title = title;
        this.comment = comment;
        this.price = price;
        this.deadLine = deadLine;
        this.postImage = postImage;
        this.isSold = isSold;
    }
}
