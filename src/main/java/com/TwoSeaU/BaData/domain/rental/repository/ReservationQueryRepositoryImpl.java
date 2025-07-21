package com.TwoSeaU.BaData.domain.rental.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.rental.entity.QReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.store.entity.QStore;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRentalResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

	private final JPAQueryFactory queryFactory;

	public CursorPageResponse<GetRentalResponse> getAllRentalsByCursor(final Long cursor, final int size, final Long userId) {
		QReservation qReservation = QReservation.reservation;
		QStore qStore = QStore.store;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReservation.id.lt(cursor));
		}

		where.and(qReservation.user.id.eq(userId));

		List<Reservation> fetchedList = queryFactory.selectFrom(qReservation)
			.leftJoin(qReservation.store, qStore).fetchJoin()
			.where(where)
			.orderBy(qReservation.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = fetchedList.size() > size;
		List<Reservation> qReservationList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		List<GetRentalResponse> responseList = qReservationList.stream()
			.map(reservation -> {
				Store store = reservation.getStore();
				if(store == null) {
					throw new GeneralException(StoreException.CANT_FIND_STORE);
				}

				return GetRentalResponse.from(reservation, store);
			})
			.toList();

		Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}