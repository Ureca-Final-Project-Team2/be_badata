package com.TwoSeaU.BaData.domain.user.dto.response;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetLikesPostResponse {
	private Long postId;
	private PostCategory postCategory;
	private String partner;
	private String title;
	private Integer price;
	private Long postLikes;
	private String postImage;
	private Boolean isSold;

	public static GetLikesPostResponse from(final Post post, final Long postLikes) {
		return GetLikesPostResponse.builder()
			.postId(post.getId())
			.postCategory(post instanceof Gifticon ? PostCategory.GIFTICON : PostCategory.DATA)
			.partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
			.title(post.getTitle())
			.price(post.getPrice())
			.postLikes(postLikes)
			.postImage(post.getPostImage())
			.isSold(post.getIsSold())
			.build();
	}
}
