package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.QPost;
import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final PostLikesRepository postLikesRepository;

	@Override
	public CursorPageResponse<GetSaleResponse> getAllSalesByCursor(PostCategory postCategory, Boolean isSold, Long cursor, int size, Long userId) {
		QPost qpost = QPost.post;
		BooleanBuilder where = new BooleanBuilder();

		if(postCategory != null) {
			if(postCategory == PostCategory.GIFTICON) {
				where.and(qpost.instanceOf(Gifticon.class));
			} else if(postCategory == PostCategory.DATA) {
				where.and(qpost.instanceOf(Data.class));
			}
		}

		if(isSold != null) {
			where.and(qpost.isSold.eq(isSold));
		}

		if(cursor != null) {
			where.and(qpost.id.lt(cursor));
		}

		where.and(qpost.seller.id.eq(userId));

		List<Post> posts = queryFactory.selectFrom(qpost)
			.where(where)
			.orderBy(qpost.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = posts.size() > size;
		if(hasNext) posts.remove(size);

		List<GetSaleResponse> getSaleResponseList = posts.stream()
			.map(post -> {
				int likesCount = postLikesRepository.countByPostId(post.getId());
				return GetSaleResponse.from(post, likesCount);
			})
			.toList();

		Long nextCursor = getSaleResponseList.isEmpty() ? null : posts.get(posts.size() - 1).getId();

		return CursorPageResponse.of(getSaleResponseList, nextCursor, hasNext);
	}
}
