package com.TwoSeaU.BaData.domain.store.repository;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.store.dto.projection.StoreWithDistanceProjection;
import com.TwoSeaU.BaData.domain.store.dto.request.DeviceSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreMapSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.request.StoreSearchRequest;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreDeviceWithRemainCountResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceAndDistanceResponse;
import com.TwoSeaU.BaData.domain.store.dto.response.ShowStoreWithLeftDeviceResponse;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.service.GeoUtils;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.config.QueryDSLConfig;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDSLConfig.class)
class StoreDeviceRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName
            .parse("postgis/postgis:15-3.3")
            .asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @AfterAll
    static void stopPostgres() {

        postgresContainer.close();
    }

    @Autowired
    private StoreDeviceRepository storeDeviceRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeviceReservationRepository deviceReservationRepository;

    @Autowired
    private UserRepository userRepository;



    @BeforeEach
    public void init(){

        Store store1 = Store.of(
                "서울 강남점",
                GeoUtils.makeByCoordinate(127.0276, 37.4979),  // 강남역
                "02-1234-5678",
                "서울특별시 강남구 강남대로 396",
                10,
                "https://example.com/image1.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        Store store2 = Store.of(
                "부산 해운대점",
                GeoUtils.makeByCoordinate(129.1580, 35.1632),  // 해운대
                "051-876-4321",
                "부산광역시 해운대구 해운대해변로 140",
                8,
                "https://example.com/image2.jpg",
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );

        Store store3 = Store.of(
                "제주공항점",
                GeoUtils.makeByCoordinate(126.4931, 33.5064),  // 제주국제공항
                "064-742-1111",
                "제주특별자치도 제주시 공항로 2",
                12,
                "https://example.com/image3.jpg",
                LocalTime.of(8, 30),
                LocalTime.of(20, 0)
        );

        storeRepository.save(store1);
        storeRepository.save(store2);
        storeRepository.save(store3);

        Device deviceA = deviceRepository.save(Device.of(
                "5G 무제한 기기",
                1000,      // dataMaxSpeed in Mbps
                5,         // supportDevicesCount
                true,      // is5G
                "https://example.com/images/5g-unlimited.png"
        ));

        Device deviceB = deviceRepository.save(Device.of(
                "LTE 기본형",
                150,
                3,
                false,
                "https://example.com/images/lte-basic.png"
        ));

        Device deviceC = deviceRepository.save(Device.of(
                "5G 프리미엄",
                2000,
                10,
                true,
                "https://example.com/images/5g-premium.png"
        ));

        Device deviceD = deviceRepository.save(Device.of(
                "LTE 저가형",
                100,
                2,
                false,
                "https://example.com/images/lte-low.png"
        ));

        deviceRepository.save(deviceA);
        deviceRepository.save(deviceB);
        deviceRepository.save(deviceC);
        deviceRepository.save(deviceD);

        StoreDevice storeDevice1A = storeDeviceRepository.save(StoreDevice.of(store1,deviceA,5,3900,20));
        StoreDevice storeDevice1B = storeDeviceRepository.save(StoreDevice.of(store1,deviceB,10,7800,10));
        StoreDevice storeDevice2C = storeDeviceRepository.save(StoreDevice.of(store2,deviceC, 20, 10900, 99));

        User user = User.of("jinu","kakao1234","pw",20,20, Role.GENERAL, SocialType.KAKAO,
                "dionisos198@gmail.com","imageUrl.com", null);

        userRepository.save(user);

        Reservation reservation = Reservation.of(user, store1, LocalDateTime.of(2025,7,1,0,0,0),
                LocalDateTime.of(2025,7,3,0,0,0));

        Reservation reservation1 = Reservation.of(user, store2, LocalDateTime.of(2025,8,1,0,0,0),
                LocalDateTime.of(2025,8,3,0,0,0));

        Reservation reservation2 = Reservation.of(user, store1, LocalDateTime.of(2025,8,1,0,0,0),
                LocalDateTime.of(2025,8,3,0,0,0));

        reservationRepository.save(reservation);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        DeviceReservation deviceReservation1 = DeviceReservation.of(reservation, storeDevice1A, 3);
        DeviceReservation deviceReservation2 = DeviceReservation.of(reservation, storeDevice1B,5);
        DeviceReservation deviceReservation3 = DeviceReservation.of(reservation1, storeDevice2C, 20);
        DeviceReservation deviceReservation4 = DeviceReservation.of(reservation2, storeDevice1A , 4);

        deviceReservationRepository.save(deviceReservation1);
        deviceReservationRepository.save(deviceReservation2);
        deviceReservationRepository.save(deviceReservation3);
        deviceReservationRepository.save(deviceReservation4);
    }

    @Nested
    @DisplayName("특정 범위 내에 있는 가맹점을 확인할 때")
    class Describe_findStoresInBoundingBox {

        @Nested
        @DisplayName("대한민국 전체 범위 조회 요청을 보낼 때")
        class Context_with_full_korea_range {

            @Test
            @DisplayName("다른 칼럼이 null 인 경우 정상적으로 서울, 부산 가맹점만 반환된다.")
            void it_returns_seoul_and_busan_only_on_null(){

                // given
                StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                        .isOpeningNow(null)
                        .swLat(33.0)
                        .swLng(124.6)
                        .neLat(38.6)
                        .neLng(131.9)
                        .rentalStartDate(null)
                        .rentalEndDate(null)
                        .reviewRating(null)
                        .minPrice(null)
                        .maxPrice(null)
                        .dataCapacity(List.of())
                        .is5G(null)
                        .maxSupportConnection(List.of())
                        .build();

                //when
                List<ShowStoreWithLeftDeviceResponse> storeWithLeftDeviceResponses = storeDeviceRepository.findStoresInBoundingBox(
                        request, "kakao1234");

                //then
                assertThat(storeWithLeftDeviceResponses.size()).isEqualTo(2);
                assertThat(storeWithLeftDeviceResponses.get(1).getStore().getName()).isEqualTo(
                        "부산 해운대점");
                assertThat(storeWithLeftDeviceResponses.get(1).getLeftDeviceCount()).isEqualTo(20);
                assertThat(storeWithLeftDeviceResponses.get(0).getStore().getName()).isEqualTo(
                        "서울 강남점");
                assertThat(storeWithLeftDeviceResponses.get(0).getLeftDeviceCount()).isEqualTo(15);
            }

            @Test
            @DisplayName("렌탈 대여 기간이 겹칠 경우 남은 대여량이 정상적으로 반환되어야 한다.")
            void it_returns_left_count_on_rental_date(){

                // given
                StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                        .isOpeningNow(null)
                        .swLat(33.0)
                        .swLng(124.6)
                        .neLat(38.6)
                        .neLng(131.9)
                        .rentalStartDate(LocalDateTime.of(2025,6,30,0,0))
                        .rentalEndDate(LocalDateTime.of(2025,7,2,0,0))
                        .reviewRating(null)
                        .minPrice(null)
                        .maxPrice(null)
                        .dataCapacity(List.of())
                        .is5G(null)
                        .maxSupportConnection(List.of())
                        .build();

                //when
                List<ShowStoreWithLeftDeviceResponse> storeWithLeftDeviceResponses = storeDeviceRepository.findStoresInBoundingBox(
                        request, "kakao1234");

                //then
                assertThat(storeWithLeftDeviceResponses.size()).isEqualTo(2);
                assertThat(storeWithLeftDeviceResponses.get(0).getStore().getName()).isEqualTo(
                        "서울 강남점");
                assertThat(storeWithLeftDeviceResponses.get(0).getLeftDeviceCount()).isEqualTo(7);
                assertThat(storeWithLeftDeviceResponses.get(1).getStore().getName()).isEqualTo(
                        "부산 해운대점");
                assertThat(storeWithLeftDeviceResponses.get(1).getLeftDeviceCount()).isEqualTo(20);
            }

            @Test
            @DisplayName("렌탈 대여 기간이 겹치고 5G인 경우 남은 대여량이 정상적으로 반환되어야 한다.")
            void it_returns_left_count_on_rental_date_5G(){

                // given
                StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                        .isOpeningNow(null)
                        .swLat(33.0)
                        .swLng(124.6)
                        .neLat(38.6)
                        .neLng(131.9)
                        .rentalStartDate(LocalDateTime.of(2025,6,30,0,0))
                        .rentalEndDate(LocalDateTime.of(2025,7,2,0,0))
                        .reviewRating(null)
                        .minPrice(null)
                        .maxPrice(null)
                        .dataCapacity(List.of())
                        .is5G(true)
                        .maxSupportConnection(List.of())
                        .build();

                //when
                List<ShowStoreWithLeftDeviceResponse> storeWithLeftDeviceResponses = storeDeviceRepository.findStoresInBoundingBox(
                        request, "kakao1234");

                //then
                assertThat(storeWithLeftDeviceResponses.get(0).getStore().getName()).isEqualTo(
                        "서울 강남점");
                assertThat(storeWithLeftDeviceResponses.get(0).getLeftDeviceCount()).isEqualTo(2);
                assertThat(storeWithLeftDeviceResponses.get(1).getStore().getName()).isEqualTo(
                        "부산 해운대점");
                assertThat(storeWithLeftDeviceResponses.get(1).getLeftDeviceCount()).isEqualTo(20);
                assertThat(storeWithLeftDeviceResponses.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("렌탈 대여 기간이 겹치고 모두 예약이 되었다면 나타나지 않아야 한다.")
            void it_returns_rental_date_dont_show(){

                // given
                StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                        .isOpeningNow(null)
                                .swLat(33.0)
                                .swLng(124.6)
                                .neLat(38.6)
                                .neLng(131.9)
                                .rentalStartDate(LocalDateTime.of(2025,7,30,0,0))
                                .rentalEndDate(LocalDateTime.of(2025,8,3,0,0))
                                .reviewRating(null)
                                .minPrice(null)
                                .maxPrice(null)
                                .dataCapacity(List.of())
                                .is5G(null)
                                .maxSupportConnection(List.of())
                        .build();

                //when
                List<ShowStoreWithLeftDeviceResponse> storeWithLeftDeviceResponses = storeDeviceRepository.findStoresInBoundingBox(
                        request, "kakao1234");

                //then
                assertThat(storeWithLeftDeviceResponses.size()).isEqualTo(1);
                assertThat(storeWithLeftDeviceResponses.get(0).getStore().getName()).isEqualTo(
                        "서울 강남점");
                assertThat(storeWithLeftDeviceResponses.get(0).getLeftDeviceCount()).isEqualTo(11);
            }
        }
        @Nested
        @DisplayName("대한민국 일부 지역 요청을 보낼 때")
        class Context_with_partial_korea_range {

            @Test
            @DisplayName("렌탈 대여 기간이 겹치고 5G인 경우 남은 대여량이 정상적으로 반환되어야 한다.")
            void it_returns_left_count_on_rental_date_5G(){

                // given
                StoreMapSearchRequest request = StoreMapSearchRequest.builder()
                        .isOpeningNow(null)
                        .swLat(37.413294)
                        .swLng(126.734086)
                        .neLat(37.715133)
                        .neLng(127.269311)
                        .rentalStartDate(LocalDateTime.of(2025,6,30,0,0))
                        .rentalEndDate(LocalDateTime.of(2025,7,2,0,0))
                        .reviewRating(null)
                        .minPrice(null)
                        .maxPrice(null)
                        .dataCapacity(List.of())
                        .is5G(true)
                        .maxSupportConnection(List.of())
                        .build();

                //when
                List<ShowStoreWithLeftDeviceResponse> storeWithLeftDeviceResponses = storeDeviceRepository.findStoresInBoundingBox(
                        request, "kakao1234");

                //then
                assertThat(storeWithLeftDeviceResponses.get(0).getStore().getName()).isEqualTo(
                        "서울 강남점");
                assertThat(storeWithLeftDeviceResponses.get(0).getLeftDeviceCount()).isEqualTo(2);
                assertThat(storeWithLeftDeviceResponses.size()).isEqualTo(1);
            }


        }
    }

    @Nested
    @DisplayName("findStoresByPage 메서드 테스트")
    class Describe_findStoresByPage {

        @Test
        @DisplayName("서울과 부산 지점을 정상적으로 반환한다.")
        void it_returns_seoul_and_busan() {
            // given
            StoreSearchRequest request = StoreSearchRequest.builder()
                    .centerLat(36.5)
                    .centerLng(127.5)
                    .isOpeningNow(null)
                    .rentalStartDate(null)
                    .rentalEndDate(null)
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            Pageable pageable = PageRequest.of(0, 10);

            // when
            Slice<ShowStoreWithLeftDeviceAndDistanceResponse> result =
                    storeDeviceRepository.findStoresByPage(request, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.hasNext()).isFalse();

            var stores = result.getContent();

            assertThat(stores)
                    .extracting(store -> store.getStore().getName())
                    .containsExactly("서울 강남점", "부산 해운대점");

            assertThat(stores)
                    .extracting(ShowStoreWithLeftDeviceAndDistanceResponse::getLeftDeviceCount)
                    .containsExactly(15, 20);
        }

        @Test
        @DisplayName("렌탈 기간을 주면 남은 수량이 반영된다.")
        void it_reflects_left_device_with_rental_date() {
            StoreSearchRequest request = StoreSearchRequest.builder()
                    .centerLat(36.5)
                    .centerLng(127.5)
                    .isOpeningNow(null)
                    .rentalStartDate(LocalDateTime.of(2025, 7, 1, 0, 0))
                    .rentalEndDate(LocalDateTime.of(2025, 7, 2, 0, 0))
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            Pageable pageable = PageRequest.of(0, 10);

            Slice<ShowStoreWithLeftDeviceAndDistanceResponse> result =
                    storeDeviceRepository.findStoresByPage(request, pageable);

            assertThat(result.getContent()).hasSize(2);

            var seoul = result.getContent().get(0);
            var busan = result.getContent().get(1);

            assertThat(seoul.getStore().getName()).isEqualTo("서울 강남점");
            assertThat(seoul.getLeftDeviceCount()).isEqualTo(7);

            assertThat(busan.getLeftDeviceCount()).isEqualTo(20);
        }

        @Test
        @DisplayName("5G 조건으로 조회하면 5G 기기만 포함된다.")
        void it_filters_by_5g_flag() {
            StoreSearchRequest request = StoreSearchRequest.builder()
                    .centerLat(36.5)
                    .centerLng(127.5)
                    .isOpeningNow(null)
                    .rentalStartDate(LocalDateTime.of(2025, 7, 1, 0, 0))
                    .rentalEndDate(LocalDateTime.of(2025, 7, 2, 0, 0))
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(true)
                    .maxSupportConnection(List.of())
                    .build();

            Pageable pageable = PageRequest.of(0, 10);

            Slice<ShowStoreWithLeftDeviceAndDistanceResponse> result =
                    storeDeviceRepository.findStoresByPage(request, pageable);

            assertThat(result.getContent()).hasSize(2);

            var seoul = result.getContent().get(0);
            var busan = result.getContent().get(1);

            assertThat(seoul.getStore().getName()).isEqualTo("서울 강남점");
            assertThat(seoul.getLeftDeviceCount()).isEqualTo(2);

            assertThat(busan.getLeftDeviceCount()).isEqualTo(20);
        }

        @Test
        @DisplayName("렌탈 기간에 전량 예약된 지점 또한 나타나야 않는다.")
        void it_excludes_fully_reserved_stores() {
            StoreSearchRequest request = StoreSearchRequest.builder()
                    .centerLat(36.5)
                    .centerLng(127.5)
                    .isOpeningNow(null)
                    .rentalStartDate(LocalDateTime.of(2025, 8, 1, 0, 0))
                    .rentalEndDate(LocalDateTime.of(2025, 8, 3, 0, 0))
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            Pageable pageable = PageRequest.of(0, 10);

            Slice<ShowStoreWithLeftDeviceAndDistanceResponse> result =
                    storeDeviceRepository.findStoresByPage(request, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStore().getName()).isEqualTo("서울 강남점");
            assertThat(result.getContent().get(0).getLeftDeviceCount()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("특정 가맹점에서 적절한 기기 목록을 조회할 때")
    class Describe_findProperDevicesByStore {

        @Test
        @DisplayName("서울 강남점의 모든 기기를 정상적으로 반환한다.")
        void it_returns_all_devices_in_seoul_store() {
            // given
            Store store = storeRepository.findAll().stream()
                    .filter(s -> s.getName().equals("서울 강남점"))
                    .findFirst()
                    .orElseThrow();

            DeviceSearchRequest request = DeviceSearchRequest.builder()
                    .isOpeningNow(null)
                    .rentalStartDate(null)
                    .rentalEndDate(null)
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            // when
            List<ShowStoreDeviceWithRemainCountResponse> result = storeDeviceRepository.findProperDevicesByStore(request, store.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(r -> r.getStoreDevice().getDevice().getName())
                    .containsExactlyInAnyOrder("5G 무제한 기기", "LTE 기본형");

            assertThat(result)
                    .extracting(ShowStoreDeviceWithRemainCountResponse::getLeftCount)
                    .containsExactlyInAnyOrder(5, 10);
        }

        @Test
        @DisplayName("5G 조건을 주면 5G 기기만 반환된다.")
        void it_returns_only_5g_devices() {
            Store store = storeRepository.findAll().stream()
                    .filter(s -> s.getName().equals("서울 강남점"))
                    .findFirst()
                    .orElseThrow();

            DeviceSearchRequest request = DeviceSearchRequest.builder()
                    .isOpeningNow(null)
                    .rentalStartDate(LocalDateTime.of(2025, 7, 1, 0, 0))
                    .rentalEndDate(LocalDateTime.of(2025, 7, 2, 0, 0))
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(true)
                    .maxSupportConnection(List.of())
                    .build();

            List<ShowStoreDeviceWithRemainCountResponse> result = storeDeviceRepository.findProperDevicesByStore(request, store.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStoreDevice().getDevice().getName()).isEqualTo("5G 무제한 기기");
            assertThat(result.get(0).getLeftCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("렌탈 기간에 기기가 모두 예약되었을 경우 나타나지 않아야 한다.")
        void it_returns_zero_when_fully_reserved() {
            Store store = storeRepository.findAll().stream()
                    .filter(s -> s.getName().equals("부산 해운대점"))
                    .findFirst()
                    .orElseThrow();

            DeviceSearchRequest request = DeviceSearchRequest.builder()
                    .isOpeningNow(null)
                    .rentalStartDate(LocalDateTime.of(2025, 8, 1, 0, 0))
                    .rentalEndDate(LocalDateTime.of(2025, 8, 3, 0, 0))
                    .reviewRating(null)
                    .minPrice(null)
                    .maxPrice(null)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            List<ShowStoreDeviceWithRemainCountResponse> result = storeDeviceRepository.findProperDevicesByStore(request, store.getId());

            assertThat(result).hasSize(0);
        }

        @Test
        @DisplayName("가격 필터(min, max)가 적용되면 범위 내 기기만 반환된다.")
        void it_filters_by_price_range() {
            Store store = storeRepository.findAll().stream()
                    .filter(s -> s.getName().equals("서울 강남점"))
                    .findFirst()
                    .orElseThrow();

            DeviceSearchRequest request = DeviceSearchRequest.builder()
                    .isOpeningNow(null)
                    .rentalStartDate(null)
                    .rentalEndDate(null)
                    .reviewRating(null)
                    .minPrice(5000)
                    .maxPrice(8000)
                    .dataCapacity(List.of())
                    .is5G(null)
                    .maxSupportConnection(List.of())
                    .build();

            List<ShowStoreDeviceWithRemainCountResponse> result = storeDeviceRepository.findProperDevicesByStore(request, store.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStoreDevice().getDevice().getName()).isEqualTo("LTE 기본형");
        }
    }



}