package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetLikesPostResponse {
	private Long id;
	private Long postId;
	private PostCategory postCategory;
	private String partner;
	private MobileCarrier mobileCarrier;
	private String title;
	private BigDecimal price;
	private int postLikes;
	private String postImage;
	private Boolean isSold;

	public static GetLikesPostResponse from(final PostLikes postLikes, final Post post, final int postLikesCnt) {
		return GetLikesPostResponse.builder()
			.id(postLikes.getId())
			.postId(post.getId())
			.postCategory(post instanceof Gifticon ? PostCategory.GIFTICON : PostCategory.DATA)
			.partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
			.mobileCarrier(post instanceof Data data ? data.getMobileCarrier() : null)
			.title(post.getTitle())
			.price(post.getPrice())
			.postLikes(postLikesCnt)
			.postImage(post.getPostImage())
			.isSold(post.getIsSold())
			.build();
	}
}
