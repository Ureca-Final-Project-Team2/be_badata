package com.TwoSeaU.BaData.domain.rental.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.rental.entity.QReStock;
import com.TwoSeaU.BaData.domain.rental.entity.ReStock;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.QDevice;
import com.TwoSeaU.BaData.domain.store.entity.QStore;
import com.TwoSeaU.BaData.domain.store.entity.QStoreDevice;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.user.dto.response.GetRestockResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReStockQueryRepositoryImpl implements ReStockQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetRestockResponse> getAllRestocksByCursor(final Long cursor, final int size, final Long userId) {

		final QReStock qReStock = QReStock.reStock;
		final QStoreDevice qStoreDevice = QStoreDevice.storeDevice;
		final QDevice qDevice = QDevice.device;
		final QStore qStore = QStore.store;
		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReStock.id.lt(cursor));
		}

		where.and(qReStock.user.id.eq(userId));

		final List<ReStock> fetchedList = queryFactory.selectFrom(qReStock)
			.join(qReStock.storeDevice, qStoreDevice).fetchJoin()
			.join(qStoreDevice.device, qDevice).fetchJoin()
			.join(qStoreDevice.store, qStore).fetchJoin()
			.where(where)
			.orderBy(qReStock.id.desc())
			.limit(size + 1)
			.fetch();

		final boolean hasNext = fetchedList.size() > size;
		final List<ReStock> qReStockList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		final List<GetRestockResponse> responseList = qReStockList.stream()
			.map(reStock -> {
				final StoreDevice storeDevice = reStock.getStoreDevice();
				final Device device = storeDevice.getDevice();
				final Store store = reStock.getStoreDevice().getStore();
				return GetRestockResponse.from(reStock, storeDevice, device, store);
			})
			.toList();

		final Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}
