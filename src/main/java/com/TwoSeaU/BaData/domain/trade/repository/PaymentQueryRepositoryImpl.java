package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.TwoSeaU.BaData.domain.trade.entity.Payment;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.QPayment;
import com.TwoSeaU.BaData.domain.trade.entity.QPost;
import com.TwoSeaU.BaData.domain.trade.entity.QPostLikes;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.user.dto.response.GetPurchaseResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentQueryRepositoryImpl implements PaymentQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetPurchaseResponse> getAllPurchasesByCursor(Long cursor, int size, Long userId) {
		final QPayment qPayment = QPayment.payment;
		final QPost qPost = QPost.post;
		final QPostLikes qPostLikes = QPostLikes.postLikes;

		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qPayment.id.lt(cursor));
		}

		where.and(qPayment.user.id.eq(userId));
		where.and(qPayment.paymentStatus.eq(PaymentStatus.PAID));

		final List<Payment> fetchedList = queryFactory.selectFrom(qPayment)
			.join(qPayment.post, qPost).fetchJoin()
			.where(where)
			.orderBy(qPayment.id.desc())
			.limit(size + 1)
			.fetch();

		final List<Long> postIdList = fetchedList.stream()
			.map(payment -> payment.getPost().getId())
			.toList();

		Map<Long, Long> postLikesMap = queryFactory
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
		final List<Payment> qPaymentList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		List<GetPurchaseResponse> responseList = qPaymentList.stream()
			.map(payment -> {
				Post post = payment.getPost();
				int postLikes = postLikesMap.getOrDefault(post.getId(), 0L).intValue();

				return GetPurchaseResponse.from(post, payment, postLikes);
			})
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}
