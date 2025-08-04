package com.TwoSeaU.BaData.domain.store.repository;

import static com.TwoSeaU.BaData.domain.store.entity.QStoreLikes.storeLikes;

import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.springframework.stereotype.Repository;

import com.TwoSeaU.BaData.domain.store.entity.QStoreLikes;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreLikes;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.user.dto.response.GetLikesStoreResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreLikesQueryRepositoryImpl implements StoreLikesQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetLikesStoreResponse> getAllLikesStoresResponseByCursor(
		final Long cursor, final int size, final Long userId) {

		QStoreLikes qStoreLikes = QStoreLikes.storeLikes;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qStoreLikes.id.lt(cursor));
		}

		where.and(qStoreLikes.user.id.eq(userId));

		List<StoreLikes> fetchedList = queryFactory.selectFrom(qStoreLikes)
			.where(where)
			.orderBy(qStoreLikes.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = fetchedList.size() > size;
		List<StoreLikes> qStoreLikesList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		List<GetLikesStoreResponse> responseList = qStoreLikesList.stream()
			.map(storeLikes -> {
				Store store = storeLikes.getStore();
				if (store == null) {
					throw new GeneralException(StoreException.CANT_FIND_STORE);
				}

				return GetLikesStoreResponse.from(storeLikes.getId(), store);
			})
			.toList();

		Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}

	@Override
	public Set<Long> getUserLikedStoreIds(final String username) {

		if(username == null){
			return new HashSet<>();
		}

		return new HashSet<>(queryFactory
				.select(storeLikes.store.id)
				.from(storeLikes)
				.where(storeLikes.user.username.eq(username))
				.fetch());
	}
}
