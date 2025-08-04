package com.TwoSeaU.BaData.domain.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDeviceWithRemainCountResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreMapResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithMetaResponse;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreLikesRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    StoreService storeService;

    @Mock
    StoreRepository storeRepository;

    @Mock
    StoreDeviceRepository storeDeviceRepository;

    @Mock
    StoreLikesRepository storeLikesRepository;

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
            List<ShowStoreMapResponse> result = storeService.getStoreMapResponse(request,"kakao12345",3);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(store.getId());
            assertThat(result.get(0).getName()).isEqualTo(store.getName());
            assertThat(result.get(0).getLatitude()).isEqualTo(store.getPosition().getY());
            assertThat(result.get(0).getLongititude()).isEqualTo(store.getPosition().getX());
        }
    }

    @Nested
    @DisplayName("getStoresResponse()는")
    class Describe_getStoresResponse {

        @Test
        @DisplayName("Store 리스트를 ShowStoreResponse 리스트로 매핑해 ShowStoreWithMetaResponse로 반환한다.")
        void it_returns_store_response_with_meta() {
            // given
            StoreSearchRequest request = StoreSearchRequest.builder()
                    .centerLat(37.5)
                    .centerLng(127.0)
                    .isOpeningNow(true)
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

            ShowStoreWithLeftDeviceAndDistanceResponse dto = new ShowStoreWithLeftDeviceAndDistanceResponse(
                    store,
                    123.45,  // distance
                    7        // leftDeviceCount
            );

            Slice<ShowStoreWithLeftDeviceAndDistanceResponse> slice = new SliceImpl<>(
                    List.of(dto),
                    PageRequest.of(0, 10),
                    false
            );

            given(storeDeviceRepository.findStoresByPage(request, PageRequest.of(0, 10)))
                    .willReturn(slice);

            given(storeLikesRepository.getUserLikedStoreIds(any())).willReturn(new HashSet<>());

            // when
            ShowStoreWithMetaResponse response = storeService.getStoresResponse(request, PageRequest.of(0, 10), null);

            // then
            assertThat(response.getShowStoreResponses()).hasSize(1);
            ShowStoreResponse storeResponse = response.getShowStoreResponses().get(0);

            assertThat(storeResponse.getId()).isEqualTo(store.getId());
            assertThat(storeResponse.getName()).isEqualTo(store.getName());
            assertThat(storeResponse.getLatitude()).isEqualTo(store.getPosition().getY());
            assertThat(storeResponse.getLongititude()).isEqualTo(store.getPosition().getX());
            assertThat(storeResponse.getLeftDeviceCount()).isEqualTo(7);
            assertThat(response.isHasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("getStoreDeviceResponse()는")
    class Describe_getStoreDeviceResponse {

        @Test
        @DisplayName("정상적으로 매핑된 ShowDeviceInfoResponse 리스트를 반환한다.")
        void it_returns_device_info_responses() {
            // given
            Long storeId = 1L;

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

            Device device = Device.of(
                    "갤럭시Z플립",
                    500,      // dataMaxSpeed
                    5,        // supportDevicesCount
                    true,     // is5G
                    "https://image.com/device.jpg"
            );

            StoreDevice storeDevice = StoreDevice.of(
                    store,
                    device,
                    5,       // count
                    3000,    // price
                    1000     // dataCapacity
            );

            DeviceSearchRequest request = DeviceSearchRequest.builder()
                    .minPrice(1000)
                    .maxPrice(5000)
                    .dataCapacity(List.of(1000))
                    .is5G(true)
                    .rentalStartDate(LocalDateTime.now())
                    .rentalEndDate(LocalDateTime.now().plusDays(1))
                    .maxSupportConnection(List.of(5))
                    .build();

            given(storeRepository.existsById(storeId)).willReturn(true);
            given(storeDeviceRepository.findProperDevicesByStore(request, storeId)).willReturn(List.of(
                    ShowStoreDeviceWithRemainCountResponse.of(storeDevice,5)));

            // when
            List<ShowDeviceInfoResponse> result = storeService.getStoreDeviceResponse(request, storeId);

            // then
            assertThat(result).hasSize(1);
            ShowDeviceInfoResponse response = result.get(0);
            assertThat(response.getDeviceName()).isEqualTo("갤럭시Z플립");
            assertThat(response.getPrice()).isEqualTo(3000);
            assertThat(response.getDataCapacity()).isEqualTo(1000);
            assertThat(response.getLeftCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("존재하지 않는 storeId가 주어지면 예외를 던진다.")
        void it_throws_when_store_does_not_exist() {
            // given
            Long invalidStoreId = 999L;
            DeviceSearchRequest request = DeviceSearchRequest.builder().build();

            given(storeRepository.existsById(invalidStoreId)).willReturn(false);

            // when
            GeneralException ex = Assertions.assertThrows(GeneralException.class, () -> storeService.getStoreDeviceResponse(request, invalidStoreId));

            //then
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(ex.getBaseException().getMessage()).isEqualTo(StoreException.CANT_FIND_STORE.getMessage());
        }
    }

    private Point point(double lon, double lat) {

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        point.setSRID(4326);
        return point;
    }



}