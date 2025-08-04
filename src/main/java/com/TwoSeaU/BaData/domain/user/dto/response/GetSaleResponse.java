package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetSaleResponse {
	private Long postId;
	private PostCategory postCategory;
	private String partner;
	private MobileCarrier mobileCarrier;
	private String title;
	private BigDecimal price;
	private int postLikes;
	private String postImage;
	private Boolean isSold;

	public static GetSaleResponse from(final Post post, final int postLikes) {
		return GetSaleResponse.builder()
			.postId(post.getId())
			.postCategory(post instanceof Gifticon ? PostCategory.GIFTICON : PostCategory.DATA)
			.partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
			.mobileCarrier(post instanceof Data data ? data.getMobileCarrier() : null)
			.title(post.getTitle())
			.price(post.getPrice())
			.postLikes(postLikes)
			.postImage(post.getPostImage())
			.isSold(post.getIsSold())
			.build();
	}
}
