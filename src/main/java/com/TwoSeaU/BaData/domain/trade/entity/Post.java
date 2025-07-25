package com.TwoSeaU.BaData.domain.trade.entity;

import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    private LocalDate deadLine;

    private String postImage;

    private Boolean isSold;

    private Boolean isDeleted;

    public Post(final User seller, final String title, final String comment, final Integer price,
                final LocalDate deadLine, final String postImage, final Boolean isSold) {
        this.seller = seller;
        this.title = title;
        this.comment = comment;
        this.price = price;
        this.deadLine = deadLine;
        this.postImage = postImage;
        this.isSold = isSold;
        this.isDeleted = false;
    }

    public void updateCommentAndPrice(final String comment, final Integer price) {
        this.comment = comment;
        this.price = price;
    }

    public void updateCommentAndPriceAndTitle(final String comment, final Integer price, final String title) {
        this.comment = comment;
        this.price = price;
        this.title = title;
    }

    public void updateIsSold(final Boolean isSold) {
        this.isSold = isSold;
    }

    public void updateIsDeleted() {
        this.isDeleted = true;
    }
}
