package com.TwoSeaU.BaData.domain.user.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.user.dto.response.GetCoinHistoryResponse;
import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;
import com.TwoSeaU.BaData.domain.user.entity.QCoinHistory;
import com.TwoSeaU.BaData.domain.user.entity.QUser;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoinHistoryQueryRepositoryImpl implements CoinHistoryQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetCoinHistoryResponse> getAllCoinsResponse(final Long cursor, final int size, final Long userId) {

		final QCoinHistory qCoinHistory = QCoinHistory.coinHistory;
		final QUser qUser = QUser.user;
		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qCoinHistory.id.lt(cursor));
		}

		where.and(qCoinHistory.user.id.eq(userId));

		final List<CoinHistory> fetchedList = queryFactory.selectFrom(qCoinHistory)
			.join(qCoinHistory.user, qUser).fetchJoin()
			.where(where)
			.orderBy(qCoinHistory.id.desc())
			.limit(size + 1)
			.fetch();

		final boolean hasNext = fetchedList.size() > size;
		final List<CoinHistory> qCoinHistoryList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		final List<GetCoinHistoryResponse> responseList = qCoinHistoryList.stream()
			.map(GetCoinHistoryResponse::of)
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}