package com.TwoSeaU.BaData.domain.rental.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.rental.entity.QReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRentalResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

	private final JPAQueryFactory queryFactory;

	public CursorPageResponse<GetRentalResponse> getAllRentalsByCursor(final Long cursor, final int size, final Long userId) {
		QReservation qReservation = QReservation.reservation;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReservation.id.lt(cursor));
		}

		where.and(qReservation.user.id.eq(userId));

		List<Reservation> fetchedList = queryFactory.selectFrom(qReservation)
			.where(where)
			.orderBy(qReservation.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = fetchedList.size() > size;
		List<Reservation> qReservationList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		List<GetRentalResponse> responseList = qReservationList.stream()
			.map(GetRentalResponse::from)
			.toList();

		Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}