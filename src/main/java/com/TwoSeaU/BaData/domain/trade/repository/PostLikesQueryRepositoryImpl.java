package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.TwoSeaU.BaData.domain.trade.entity.*;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesPostResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostLikesQueryRepositoryImpl implements PostLikesQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetLikesPostResponse> getAllLikesPostsByCursor(final Long cursor, final int size, final Long userId) {
		final QPostLikes qPostLikes = QPostLikes.postLikes;
		final QPost qPost = QPost.post;

		final BooleanBuilder where = new BooleanBuilder();

		where.and(qPostLikes.post.isDeleted.isFalse());

		if(cursor != null) {
			where.and(qPostLikes.id.lt(cursor));
		}

		where.and(qPostLikes.user.id.eq(userId));

		final List<PostLikes> fetchedList = queryFactory.selectFrom(qPostLikes)
			.join(qPostLikes.post, qPost).fetchJoin()
			.where(where)
			.orderBy(qPostLikes.id.desc())
			.limit(size + 1)
			.fetch();

		final List<Long> postIdList = fetchedList.stream()
			.map(postLikes -> postLikes.getPost().getId())
			.toList();

		final Map<Long, Long> postLikesMap = queryFactory
			.select(qPostLikes.post.id, qPostLikes.count())
			.from(qPostLikes)
			.where(qPostLikes.post.id.in(postIdList))
			.groupBy(qPostLikes.post.id)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(0, Long.class),
				tuple -> tuple.get(1, Long.class)
			));


		final boolean hasNext = fetchedList.size() > size;
		final List<PostLikes> qPostLikesList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		final List<GetLikesPostResponse> responseList = qPostLikesList.stream()
			.map(postLikes -> {
				final Post post = postLikes.getPost();
				final int postLikesCount = postLikesMap.getOrDefault(post.getId(), 0L).intValue();

				return GetLikesPostResponse.from(postLikes, post, postLikesCount);
			})
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}

	@Override
	public List<Long> findDistinctPostIdsByUserId(Long userId) {
		final QPostLikes qPostLikes = QPostLikes.postLikes;
		final QPayment qPayment = QPayment.payment;

		return queryFactory.select(qPostLikes.post.id)
				.from(qPostLikes)
				.where(
						qPostLikes.user.id.eq(userId),
						qPostLikes.post.id.notIn(
								JPAExpressions
										.select(qPayment.post.id)
										.from(qPayment)
										.where(
												qPayment.user.id.eq(userId),
												qPayment.paymentStatus.eq(PaymentStatus.PAID)
										)
						)
				)
				.fetch();
	}

	@Override
	public Map<Long, Integer> countByPostIds(final List<Long> postIds) {
		final QPostLikes qPostLikes = QPostLikes.postLikes;

		return queryFactory.select(qPostLikes.post.id, qPostLikes.count())
				.from(qPostLikes)
				.where(qPostLikes.post.id.in(postIds))
				.groupBy(qPostLikes.post.id)
				.fetch()
				.stream()
				.collect(Collectors.toMap(
						tuple -> tuple.get(0, Long.class),
						tuple -> tuple.get(1, Long.class).intValue()
				));
	}

	@Override
	public Set<Long> findLikedPostIdsByUserIdAndPostIds(final Long userId, final List<Long> postIds) {
		final QPostLikes qPostLikes = QPostLikes.postLikes;

		return new HashSet<>(queryFactory.select(qPostLikes.post.id)
                .from(qPostLikes)
                .where(
                        qPostLikes.user.id.eq(userId),
                        qPostLikes.post.id.in(postIds)
                )
                .fetch());

	}

}
