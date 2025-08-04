package com.TwoSeaU.BaData.domain.user.dto.response;

import java.math.BigDecimal;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReportResponse {
	private Long id;
	private Long postId;
	private String thumbnailUrl;
	private String title;
	private String partner;
	private MobileCarrier mobileCarrier;
	private BigDecimal price;
	private Integer postLikes;
	private Boolean isSold;

	public static GetReportResponse from(final Report report, final Post post, final Integer postLikes) {
		return GetReportResponse.builder()
			.id(report.getId())
			.postId(post.getId())
			.title(post.getTitle())
			.thumbnailUrl(post.getPostImage())
			.title(post.getTitle())
			.partner(post instanceof Gifticon gifticon ? gifticon.getPartner() : null)
			.mobileCarrier(post instanceof Data data ? data.getMobileCarrier() : null)
			.price(post.getPrice())
			.postLikes(postLikes)
			.isSold(post.getIsSold())
			.build();
	}
}
