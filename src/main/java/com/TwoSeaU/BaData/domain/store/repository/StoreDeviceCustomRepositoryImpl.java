package com.TwoSeaU.BaData.domain.store.repository;

import static com.TwoSeaU.BaData.domain.rental.entity.QDeviceReservation.deviceReservation;
import static com.TwoSeaU.BaData.domain.rental.entity.QReservation.reservation;
import static com.TwoSeaU.BaData.domain.store.entity.QStore.store;
import static com.TwoSeaU.BaData.domain.store.entity.QStoreDevice.storeDevice;
import static com.TwoSeaU.BaData.domain.store.entity.QStoreLikes.storeLikes;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDeviceWithRemainCountResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.entity.StoreLikes;
import com.TwoSeaU.BaData.domain.store.service.GeoUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;


@RequiredArgsConstructor
public class StoreDeviceCustomRepositoryImpl implements StoreDeviceCustomRepository{

    private final JPAQueryFactory queryFactory;
    private static final String review_count="reviewCount";
    public static final String distance = "distance";

    @Override
    public List<ShowStoreWithLeftDeviceResponse> findStoresInBoundingBox(final StoreMapSearchRequest storeMapSearchRequest, final String username){

        final Set<Long> userLikedStoreIds = getUserLikedStoreIds(username);

        LocalDateTime rentalStartDate = storeMapSearchRequest.getRentalStartDate();
        LocalDateTime rentalEndDate = storeMapSearchRequest.getRentalEndDate();

        if( rentalStartDate == null || rentalEndDate == null){
            rentalStartDate = LocalDateTime.now();
            rentalEndDate = LocalDate.now().atTime(23, 59, 59);
        }

        final List<Tuple> results =  queryFactory.select(
                        storeDevice.store,storeDevice.count.subtract(
                                JPAExpressions
                                        .select(deviceReservation.reservationCount.sum().coalesce(0))
                                        .from(deviceReservation)
                                        .join(deviceReservation.reservation, reservation)
                                        .where(
                                                deviceReservation.storeDevice.eq(storeDevice),
                                                reservation.rentalStartDate.loe(rentalEndDate),
                                                reservation.rentalEndDate.goe(rentalStartDate)
                                        )
                        ).sum())
                .from(storeDevice)
                .where(minPriceGoe(storeMapSearchRequest.getMinPrice()))
                .where(maxPriceLoe(storeMapSearchRequest.getMaxPrice()))
                .where(inDataCapacity(storeMapSearchRequest.getDataCapacity()))
                .where(is5GEq(storeMapSearchRequest.getIs5G()))
                .where(availableDuringPeriod(storeMapSearchRequest.getRentalStartDate(),
                        storeMapSearchRequest.getRentalEndDate()))
                .where(inMaxSupportConnection(storeMapSearchRequest.getMaxSupportConnection()))
                .where(isOpeningNow(storeMapSearchRequest.getIsOpeningNow()))
                .where(filterReviewRating(storeMapSearchRequest.getReviewRating()))
                .where(availableInBoundingBox(storeMapSearchRequest.getSwLng(),
                        storeMapSearchRequest.getSwLat(),
                        storeMapSearchRequest.getNeLng(), storeMapSearchRequest.getNeLat()))
                .groupBy(storeDevice.store)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    final Store store = tuple.get(storeDevice.store);
                    final Integer leftCount = tuple.get(1, Integer.class);
                    final boolean liked = userLikedStoreIds.contains(store.getId());

                    return new ShowStoreWithLeftDeviceResponse(store, leftCount, liked);
                })
                .toList();
    }

    @Override
    public Slice<ShowStoreWithLeftDeviceAndDistanceResponse> findStoresByPage(final StoreSearchRequest storeSearchRequest, final Pageable pageable){

        List<ShowStoreWithLeftDeviceAndDistanceResponse> content = queryFactory
                .select(Projections.constructor(ShowStoreWithLeftDeviceAndDistanceResponse.class,
                                storeDevice.store,
                                Expressions.numberTemplate(Double.class,
                                "ST_DistanceSphere({0}, ST_MakePoint({1}, {2}))",
                                store.position,
                                storeSearchRequest.getCenterLng(),
                                storeSearchRequest.getCenterLat()),
                                storeDevice.count.subtract(
                                        JPAExpressions
                                                .select(deviceReservation.reservationCount.sum().coalesce(0))
                                                .from(deviceReservation)
                                                .join(deviceReservation.reservation, reservation)
                                                .where(
                                                        deviceReservation.storeDevice.eq(storeDevice),
                                                        reservation.rentalStartDate.loe(storeSearchRequest.getRentalEndDate()),
                                                        reservation.rentalEndDate.goe(storeSearchRequest.getRentalStartDate())
                                                )
                                ).sum()))
                .from(storeDevice)
                .where(minPriceGoe(storeSearchRequest.getMinPrice()))
                .where(maxPriceLoe(storeSearchRequest.getMaxPrice()))
                .where(inDataCapacity(storeSearchRequest.getDataCapacity()))
                .where(is5GEq(storeSearchRequest.getIs5G()))
                .where(availableDuringPeriod(storeSearchRequest.getRentalStartDate(), storeSearchRequest.getRentalEndDate()))
                .where(inMaxSupportConnection(storeSearchRequest.getMaxSupportConnection()))
                .where(isOpeningNow(storeSearchRequest.getIsOpeningNow()))
                .where(filterReviewRating(storeSearchRequest.getReviewRating()))
                .groupBy(storeDevice.store)
                .orderBy(storeSort(pageable, storeSearchRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);

    }


    @Override
    public List<ShowStoreDeviceWithRemainCountResponse> findProperDevicesByStore(final DeviceSearchRequest deviceSearchRequest,
            final Long storeId) {

        LocalDateTime rentalStartDate = deviceSearchRequest.getRentalStartDate();
        LocalDateTime rentalEndDate = deviceSearchRequest.getRentalEndDate();

        if( rentalStartDate == null || rentalEndDate == null){
            rentalStartDate = LocalDateTime.now();
            rentalEndDate = LocalDate.now().atTime(23, 59, 59);
        }

        NumberExpression<Integer> reservedCountSum = new CaseBuilder()
                .when(reservation.rentalStartDate.loe(rentalEndDate)
                        .and(reservation.rentalEndDate.goe(rentalStartDate)))
                .then(deviceReservation.reservationCount)
                .otherwise(0)
                .sum();

        return queryFactory
                .select(Projections.constructor(ShowStoreDeviceWithRemainCountResponse.class,
                        storeDevice,
                        storeDevice.count.subtract(reservedCountSum)
                ))
                .from(storeDevice)
                .leftJoin(deviceReservation).on(deviceReservation.storeDevice.eq(storeDevice))
                .leftJoin(deviceReservation.reservation, reservation)
                .where(minPriceGoe(deviceSearchRequest.getMinPrice()))
                .where(maxPriceLoe(deviceSearchRequest.getMaxPrice()))
                .where(inDataCapacity(deviceSearchRequest.getDataCapacity()))
                .where(is5GEq(deviceSearchRequest.getIs5G()))
                .where(availableDuringPeriod(rentalStartDate, rentalEndDate))
                .where(inMaxSupportConnection(deviceSearchRequest.getMaxSupportConnection()))
                .where(isOpeningNow(deviceSearchRequest.getIsOpeningNow()))
                .where(filterReviewRating(deviceSearchRequest.getReviewRating()))
                .where(storeDevice.store.id.eq(storeId))
                .groupBy(storeDevice.id)
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

    private OrderSpecifier<?> storeSort(final Pageable pageable, final StoreSearchRequest storeSearchRequest){

        if(!pageable.getSort().isEmpty()){

            for(Sort.Order order: pageable.getSort()){
                Order direction  = order.getDirection().isAscending()? Order.ASC:Order.DESC;

                switch (order.getProperty()){

                    case review_count:
                        return new OrderSpecifier(direction,store.reviewCount);

                    case distance:

                        if(storeSearchRequest.getCenterLng() == null || storeSearchRequest.getCenterLat() == null){
                            return new OrderSpecifier(Order.DESC,store.id);
                        }

                        return new OrderSpecifier<>(
                                direction,
                                Expressions.numberTemplate(Double.class,
                                        "ST_DistanceSphere({0}, {1})",
                                        store.position,
                                        Expressions.constant(GeoUtils.makeByCoordinate(storeSearchRequest.getCenterLng(),
                                                                                       storeSearchRequest.getCenterLat()))
                                )
                        );

                }
            }
        }

        return new OrderSpecifier(Order.DESC, store.id);
    }

    private Set<Long> getUserLikedStoreIds(final String username) {

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
