package com.TwoSeaU.BaData.domain.rental.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.rental.entity.QReStock;
import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.QDevice;
import com.TwoSeaU.BaData.domain.store.entity.QStoreDevice;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRestockResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReStockQueryRepositoryImpl implements ReStockQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetRestockResponse> getAllRestocksByCursor(Long cursor, int size, Long userId) {

		final QReStock qReStock = QReStock.reStock;
		final QStoreDevice qStoreDevice = QStoreDevice.storeDevice;
		final QDevice qDevice = QDevice.device;
		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReStock.id.lt(cursor));
		}

		where.and(qReStock.user.id.eq(userId));

		final List<ReStock> fetchedList = queryFactory.selectFrom(qReStock)
			.leftJoin(qReStock.storeDevice, qStoreDevice).fetchJoin()
			.leftJoin(qStoreDevice.device, qDevice).fetchJoin()
			.where(where)
			.orderBy(qReStock.id.desc())
			.limit(size + 1)
			.fetch();

		final boolean hasNext = fetchedList.size() > size;
		final List<ReStock> qReStockList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		final List<GetRestockResponse> responseList = qReStockList.stream()
			.map(reStock -> {
				StoreDevice storeDevice = reStock.getStoreDevice();
				if(storeDevice == null) throw new GeneralException(StoreException.CANT_FIND_STORE_DEVICE);
				Device device = storeDevice.getDevice();
				if(device == null) throw new GeneralException(StoreException.CANT_FIND_STORE_DEVICE);

				return GetRestockResponse.from(reStock, storeDevice, device);
			})
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}
