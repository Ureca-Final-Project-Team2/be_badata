package com.TwoSeaU.BaData.domain.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    StoreService storeService;

    @Mock
    StoreRepository storeRepository;

    @Mock
    StoreDeviceRepository storeDeviceRepository;

    @Nested
    @DisplayName("getStoreMapResponse()는")
    class Describe_getStoreMapResponse {

        @Test
        @DisplayName("Store 리스트를 ShowStoreMapResponse 리스트로 매핑해 반환한다.")
        void it_returns_store_map_responses() {
            // given
            StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                    .isOpeningNow(true)
                    .swLat(37.0)
                    .swLng(127.0)
                    .neLat(38.0)
                    .neLng(128.0)
                    .rentalStartDate(LocalDateTime.now())
                    .rentalEndDate(LocalDateTime.now().plusDays(1))
                    .reviewRating(4.5)
                    .minPrice(1000)
                    .maxPrice(10000)
                    .dataCapacity(List.of(5000))
                    .is5G(true)
                    .maxSupportConnection(List.of(5))
                    .build();

            Store store = Store.of(
                    "테스트매장",
                    point(127.0338, 37.5580),
                    "010-1234-5678",
                    "서울시 강남구",
                    5,
                    "https://image.com/img.jpg",
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0)
            );

            List<ShowStoreWithLeftDeviceResponse> storeList = List.of(store).stream().map(store1 ->
                 new ShowStoreWithLeftDeviceResponse(store1,5,false)
            ).toList();

            given(storeDeviceRepository.findStoresInBoundingBox(request,"kakao12345"))
                    .willReturn(storeList);

            // when
            List<ShowStoreMapResponse> result = storeService.getStoreMapResponse(request,"kakao12345");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(store.getId());
            assertThat(result.get(0).getName()).isEqualTo(store.getName());
            assertThat(result.get(0).getLatitude()).isEqualTo(store.getPosition().getY());
            assertThat(result.get(0).getLongititude()).isEqualTo(store.getPosition().getX());
        }
    }

    private Point point(double lon, double lat) {

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        point.setSRID(4326);
        return point;
    }



}