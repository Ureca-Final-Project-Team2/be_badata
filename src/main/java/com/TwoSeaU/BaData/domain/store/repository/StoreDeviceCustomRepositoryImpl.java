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
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.entity.Store;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@RequiredArgsConstructor
public class StoreDeviceCustomRepositoryImpl implements StoreDeviceCustomRepository{

    private final JPAQueryFactory queryFactory;
    private static final String review_count="reviewCount";
    public static final String distance = "distance";
    public static final String likeCount = "likeCount";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<ShowStoreWithLeftDeviceResponse> findStoresInBoundingBox(final StoreMapSearchRequest storeMapSearchRequest, final String username){

        final Set<Long> userLikedStoreIds = getUserLikedStoreIds(username);

        LocalDateTime rentalStartDate = storeMapSearchRequest.getRentalStartDate();
        LocalDateTime rentalEndDate = storeMapSearchRequest.getRentalEndDate();

        if( rentalStartDate == null || rentalEndDate == null){
            rentalStartDate = LocalDateTime.of(2100,3,1,0,0);
            rentalEndDate = LocalDateTime.of(2100,3,1,0,1);
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

        LocalDateTime rentalStartDate = storeSearchRequest.getRentalStartDate();
        LocalDateTime rentalEndDate = storeSearchRequest.getRentalEndDate();

        if( rentalStartDate == null || rentalEndDate == null){
            rentalStartDate = LocalDateTime.of(2100,3,1,0,0);
            rentalEndDate = LocalDateTime.of(2100,3,1,0,1);
        }

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
                                                        reservation.rentalStartDate.loe(rentalEndDate),
                                                        reservation.rentalEndDate.goe(rentalStartDate)
                                                )
                                ).sum()))
                .from(storeDevice)
                .join(storeDevice.store, store)
                .leftJoin(storeLikes).on(storeLikes.store.eq(storeDevice.store))
                .where(minPriceGoe(storeSearchRequest.getMinPrice()))
                .where(maxPriceLoe(storeSearchRequest.getMaxPrice()))
                .where(inDataCapacity(storeSearchRequest.getDataCapacity()))
                .where(is5GEq(storeSearchRequest.getIs5G()))
                .where(availableDuringPeriod(rentalStartDate, rentalEndDate))
                .where(inMaxSupportConnection(storeSearchRequest.getMaxSupportConnection()))
                .where(isOpeningNow(storeSearchRequest.getIsOpeningNow()))
                .where(filterReviewRating(storeSearchRequest.getReviewRating()))
                .groupBy(storeDevice.store)
                .orderBy(storeSort(pageable, storeSearchRequest),distanceOrderSpecifier(storeSearchRequest))
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
            rentalStartDate = LocalDateTime.of(2100,3,1,0,0);
            rentalEndDate = LocalDateTime.of(2100,3,1,0,1);
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

                                if (storeSearchRequest.getCenterLng() == null || storeSearchRequest.getCenterLat() == null) {
                                    return new OrderSpecifier<>(Order.DESC, store.id);
                                }

                                return distanceOrderSpecifier(storeSearchRequest);

                            case likeCount:
                                return new OrderSpecifier<>(direction, storeLikes.count());
                        }

                }
            }
           return distanceOrderSpecifier(storeSearchRequest);
    }



    private OrderSpecifier<?> distanceOrderSpecifier(StoreSearchRequest storeSearchRequest) {
        if (storeSearchRequest.getCenterLng() == null || storeSearchRequest.getCenterLat() == null) {
            return new OrderSpecifier<>(Order.DESC, store.id); // fallback
        }

        return new OrderSpecifier<>(
                Order.ASC, // 기본 거리 오름차순
                Expressions.numberTemplate(Double.class,
                        "ST_DistanceSphere({0}, ST_MakePoint({1}, {2}))",
                        store.position,
                        storeSearchRequest.getCenterLng(),
                        storeSearchRequest.getCenterLat()
                )
        );
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

    @Override
    public List<ShowStoreMapResponse> findClustersDynamically(final StoreMapSearchRequest request,final int eps, final int minPoints) {
        StringBuilder sql = new StringBuilder("""
        WITH filtered AS (
            SELECT
                s.id,
                s.name,
                s.phone_number,
                s.detail_address,
                s.start_time,
                s.end_time,
                s.review_count,
                s.review_rating,
                s.available_device,
                s.store_image,
                s.created_at,
                s.updated_at,
                ST_Transform(s.position, 5179) AS pos_5179,
                SUM(
                    sd.count - COALESCE((
                        SELECT SUM(dr.reservation_count)
                        FROM device_reservation dr
                        JOIN reservation r ON r.id = dr.reservation_id
                        WHERE dr.store_device_id = sd.id
                          AND r.rental_start_date <= :rentalEndDate
                          AND r.rental_end_date >= :rentalStartDate
                    ), 0)
                ) AS available_count
            FROM store_device sd
            JOIN store s ON s.id = sd.store_id
            JOIN device d ON d.id = sd.device_id
            WHERE (
                (
                    SELECT SUM(dr2.reservation_count)
                    FROM device_reservation dr2
                    JOIN reservation r2 ON r2.id = dr2.reservation_id
                    WHERE dr2.store_device_id = sd.id
                      AND r2.rental_start_date <= :rentalEndDate
                      AND r2.rental_end_date >= :rentalStartDate
                ) IS NULL
                OR (
                    SELECT SUM(dr3.reservation_count)
                    FROM device_reservation dr3
                    JOIN reservation r3 ON r3.id = dr3.reservation_id
                    WHERE dr3.store_device_id = sd.id
                      AND r3.rental_start_date <= :rentalEndDate
                      AND r3.rental_end_date >= :rentalStartDate
                ) < sd.count
            )
    """);

        Map<String, Object> params = new HashMap<>();
        params.put("eps", eps);
        params.put("minPoints", minPoints);

        // 날짜 기본값 설정
        params.put("rentalStartDate", Optional.ofNullable(request.getRentalStartDate()).orElse(LocalDateTime.of(2100, 1, 1, 0, 0)));
        params.put("rentalEndDate", Optional.ofNullable(request.getRentalEndDate()).orElse(LocalDateTime.of(2100, 1, 2, 0, 0)));

        // 동적 조건
        if (request.getMinPrice() != null) {
            sql.append(" AND sd.price >= :minPrice");
            params.put("minPrice", request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            sql.append(" AND sd.price <= :maxPrice");
            params.put("maxPrice", request.getMaxPrice());
        }
        if (request.getDataCapacity() != null && !request.getDataCapacity().isEmpty()) {
            sql.append(" AND sd.data_capacity IN (:dataCapacities)");
            params.put("dataCapacities", request.getDataCapacity());
        }
        if (request.getIs5G() != null) {
            sql.append(" AND d.is5g = :is5G");
            params.put("is5G", request.getIs5G());
        }
        if (request.getMaxSupportConnection() != null && !request.getMaxSupportConnection().isEmpty()) {
            sql.append(" AND d.support_devices_count IN (:supportCounts)");
            params.put("supportCounts", request.getMaxSupportConnection());
        }
        if (request.getIsOpeningNow() != null) {
            sql.append(" AND (s.start_time <= :nowTime AND s.end_time >= :nowTime)");
            params.put("nowTime", LocalTime.now());
        }
        if (request.getReviewRating() != null) {
            sql.append(" AND s.review_rating >= :reviewRating");
            params.put("reviewRating", request.getReviewRating());
        }
        if (request.getSwLat() != null && request.getSwLng() != null && request.getNeLat() != null && request.getNeLng() != null) {
            String bboxWKT = GeoUtils.createBoundingBoxByCoordinate(
                    request.getSwLat(), request.getSwLng(),
                    request.getNeLat(), request.getNeLng()
            ).toText();
            sql.append(" AND ST_Within(s.position, ST_GeomFromText(:bbox, 4326))");
            params.put("bbox", bboxWKT);
        }


        sql.append("""
        GROUP BY s.id
        ),
        clustered AS (
            SELECT *, ST_ClusterDBSCAN(pos_5179, :eps, :minPoints) OVER () AS cluster_id
            FROM filtered
        )
        SELECT
            cluster_id,
            COUNT(*) AS store_count,
            SUM(available_count) AS available_device_count,
            ST_AsText(ST_Transform(ST_Centroid(ST_Collect(pos_5179)), 4326)) AS center
        FROM clustered
        WHERE cluster_id > 0
        GROUP BY cluster_id
        """);

        return namedParameterJdbcTemplate.query(sql.toString(), params, clusterRowMapper);
    }

    private final RowMapper<ShowStoreMapResponse> clusterRowMapper = (rs, rowNum) -> {
        Long clusterId = rs.getLong("cluster_id");
        int leftDeviceCount = rs.getInt("available_device_count");

        // center를 문자열로 받아 파싱
        String pointText = rs.getString("center");
        String[] coords = pointText.replace("POINT(", "").replace(")", "").split(" ");

        double longitude = Double.parseDouble(coords[0]);
        double latitude = Double.parseDouble(coords[1]);

        return ShowStoreMapResponse.of(clusterId, longitude, latitude, null, leftDeviceCount, false);
    };

}
