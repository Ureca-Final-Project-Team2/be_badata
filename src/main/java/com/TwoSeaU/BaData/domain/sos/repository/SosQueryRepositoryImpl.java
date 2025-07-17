package com.TwoSeaU.BaData.domain.sos.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.TwoSeaU.BaData.domain.sos.entity.QSos;
import com.TwoSeaU.BaData.domain.sos.entity.Sos;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSosResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SosQueryRepositoryImpl implements SosQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetSosResponse> getAllSosResponse(Long cursor, int size, Long userId) {
		QSos qsos = QSos.sos;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qsos.id.lt(cursor));
		}

		where.and(qsos.requester.id.eq(userId));

		List<Sos> sosList = queryFactory.selectFrom(qsos)
			.where(where)
			.orderBy(qsos.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = sosList.size() > size;
		if(hasNext) sosList.remove(size);

		List<GetSosResponse> getSosResponseList = sosList.stream()
			.map(GetSosResponse::from)
			.toList();

		Long nextCursor = getSosResponseList.isEmpty() ? null : sosList.get(sosList.size() - 1).getId();

		return CursorPageResponse.of(getSosResponseList, nextCursor, hasNext);
	}
}
