package com.TwoSeaU.BaData.domain.store.repository;

import static com.TwoSeaU.BaData.domain.rental.entity.QDeviceReservation.deviceReservation;
import static com.TwoSeaU.BaData.domain.rental.entity.QReservation.reservation;
import static com.TwoSeaU.BaData.domain.store.entity.QStore.store;
import static com.TwoSeaU.BaData.domain.store.entity.QStoreDevice.storeDevice;
import com.TwoSeaU.BaData.domain.store.dto.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.service.GeoUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class StoreDeviceCustomRepositoryImpl implements StoreDeviceCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> findStoresInBoundingBox(final StoreSearchRequest storeSearchRequest){

        return queryFactory.select(storeDevice.store)
                .from(storeDevice)
                .where(minPriceGoe(storeSearchRequest.getMinPrice()))
                .where(maxPriceLoe(storeSearchRequest.getMaxPrice()))
                .where(inDataCapacity(storeSearchRequest.getDataCapacity()))
                .where(is5GEq(storeSearchRequest.getIs5G()))
                .where(availableDuringPeriod(storeSearchRequest.getRentalStartDate(),storeSearchRequest.getRentalEndDate()))
                .where(inMaxSupportConnection(storeSearchRequest.getMaxSupportConnection()))
                .where(isOpeningNow(storeSearchRequest.getIsOpeningNow()))
                .where(filterReviewRating(storeSearchRequest.getReviewRating()))
                .where(availableInBoundingBox(storeSearchRequest.getSwLng(),storeSearchRequest.getSwLat(),
                        storeSearchRequest.getNeLng(), storeSearchRequest.getNeLat()))
                .fetch();

    }

    private BooleanExpression minPriceGoe(Integer minPrice) {
        return minPrice != null ? storeDevice.price.goe(minPrice) : null;
    }

    private BooleanExpression maxPriceLoe(Integer maxPrice) {
        return maxPrice != null ? storeDevice.price.loe(maxPrice) : null;
    }

    private BooleanExpression inDataCapacity(List<Integer> dataCapacityList) {
        return (dataCapacityList != null && !dataCapacityList.isEmpty())
                ? storeDevice.dataCapacity.in(dataCapacityList)
                : null;
    }

    private BooleanExpression inMaxSupportConnection(List<Integer> maximumConnectionList) {
        return (maximumConnectionList != null && !maximumConnectionList.isEmpty())
                ? storeDevice.device.supportDevicesCount.in(maximumConnectionList)
                : null;
    }

    private BooleanExpression is5GEq(Boolean is5G) {
        return is5G != null
                ? storeDevice.device.is5G.eq(is5G)
                : null;
    }

    private BooleanExpression isOpeningNow(Boolean openingNow) {

        if(openingNow == null){
            return null;
        }

        LocalTime now = LocalTime.now();

        BooleanExpression isOpen = storeDevice.store.startTime.loe(now)
                .and(storeDevice.store.endTime.goe(now));

        return openingNow ? isOpen : isOpen.not();
    }

    private BooleanExpression filterReviewRating(Double reviewRating) {

        return reviewRating != null
                ? storeDevice.store.reviewRating.goe(reviewRating)
                : null;
    }

    private BooleanExpression availableDuringPeriod(LocalDateTime rentalStartDate,LocalDateTime rentalEndDate) {
        if (rentalStartDate == null || rentalEndDate == null) {
            return null;
        }

        JPQLQuery<Integer> subQuery = JPAExpressions
                .select(deviceReservation.reservationCount.sum())
                .from(deviceReservation)
                .join(deviceReservation.reservation, reservation)
                .where(
                        deviceReservation.storeDevice.eq(storeDevice),
                        reservation.rentalStartDate.loe(rentalEndDate),
                        reservation.rentalEndDate.goe(rentalStartDate)
                );

        return subQuery.isNull().or(subQuery.lt(storeDevice.count));
    }

    private BooleanExpression availableInBoundingBox(Double swLng,Double swLat,Double neLng,Double neLat){

        BooleanExpression intersectsExpression = Expressions.booleanTemplate(
                "ST_Within({0}, {1}) = true",
                store.position,
                Expressions.constant(GeoUtils.createBoundingBoxByCoordinate(swLat,swLng,neLat,neLng))
        );

        return intersectsExpression;
    }

}
