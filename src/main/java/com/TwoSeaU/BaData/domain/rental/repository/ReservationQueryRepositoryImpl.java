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
	private final ReviewRepository reviewRepository;

	public CursorPageResponse<GetRentalResponse> getAllRentalsByCursor(final Long cursor, final int size, final Long userId) {
		final QReservation qReservation = QReservation.reservation;
		final QStore qStore = QStore.store;
		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReservation.id.lt(cursor));
		}

		where.and(qReservation.user.id.eq(userId));

		final List<Reservation> fetchedList = queryFactory.selectFrom(qReservation)
			.leftJoin(qReservation.store, qStore).fetchJoin()
			.where(where)
			.orderBy(qReservation.id.desc())
			.limit(size + 1)
			.fetch();

		final boolean hasNext = fetchedList.size() > size;
		final List<Reservation> qReservationList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		final List<GetRentalResponse> responseList = qReservationList.stream()
			.map(reservation -> {
				final Store store = reservation.getStore();
				if(store == null) {
					throw new GeneralException(StoreException.CANT_FIND_STORE);
				}
				final Boolean isReviewed = reviewRepository.existsByReservationId(reservation.getId());
				return GetRentalResponse.from(reservation, store, isReviewed);
			})
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}