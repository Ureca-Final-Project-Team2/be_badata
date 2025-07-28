package com.TwoSeaU.BaData.domain.rental.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.TwoSeaU.BaData.domain.rental.dto.projection.AvailableDeviceProjection;
import com.TwoSeaU.BaData.domain.rental.entity.DeviceReservation;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.repository.DeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.store.service.GeoUtils;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.config.QueryDSLConfig;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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
class DeviceReservationRepositoryTest {

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
    DeviceReservationRepository deviceReservationRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    StoreDeviceRepository storeDeviceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    private Store store1;
    private Store store2;
    private Store store3;

    private StoreDevice storeDevice1A;
    private StoreDevice storeDevice1B;

    private StoreDevice storeDevice2C;


    @BeforeEach
    public void init(){

        store1 = Store.of(
                "서울 강남점",
                GeoUtils.makeByCoordinate(127.0276, 37.4979),  // 강남역
                "02-1234-5678",
                "서울특별시 강남구 강남대로 396",
                10,
                "https://example.com/image1.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        store2 = Store.of(
                "부산 해운대점",
                GeoUtils.makeByCoordinate(129.1580, 35.1632),  // 해운대
                "051-876-4321",
                "부산광역시 해운대구 해운대해변로 140",
                8,
                "https://example.com/image2.jpg",
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );

        store3 = Store.of(
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

        storeDevice1A = storeDeviceRepository.save(StoreDevice.of(store1,deviceA,5,3900,20));
        storeDevice1B = storeDeviceRepository.save(StoreDevice.of(store1,deviceB,10,7800,10));
        storeDevice2C = storeDeviceRepository.save(StoreDevice.of(store2,deviceC, 20, 10900, 99));


        User user = User.of("jinu","kakao1234","pw",20,20, Role.GENERAL, SocialType.KAKAO,
                "dionisos198@gmail.com","imageUrl.com" );

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
    @DisplayName("findAvailableDevicesByStoreIdAndPeriod 테스트")
    class Describe_findAvailableDevicesByStoreIdAndPeriod {

        @Test
        @DisplayName("예약 기간과 겹치지 않으면 기기 수량은 전부 availableCount로 반환된다")
        void it_returns_total_if_no_overlap() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 9, 1, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 9, 2, 0, 0);

            // when
            List<AvailableDeviceProjection> result = deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(
                    store1.getId(), rentalStart, rentalEnd
            );

            // then
            assertEquals(2, result.size());
            for (AvailableDeviceProjection projection : result) {
                assertEquals(projection.getTotalCount(), projection.getAvailableCount());
            }
        }

        @Test
        @DisplayName("2025-07-01~02 기간: 첫 번째 예약(3개)만 반영되어 2개만 남는다")
        void it_subtracts_first_reservation_only() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 7, 1, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 7, 2, 0, 0);

            // when
            List<AvailableDeviceProjection> result = deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(
                    store1.getId(), rentalStart, rentalEnd
            );

            // then
            assertEquals(2, result.size());

            AvailableDeviceProjection deviceA = result.stream()
                    .filter(r -> r.getDeviceName().equals("5G 무제한 기기"))
                    .findFirst().orElseThrow();

            AvailableDeviceProjection deviceB = result.stream()
                    .filter(r -> r.getDeviceName().equals("LTE 기본형"))
                    .findFirst().orElseThrow();

            assertEquals(5, deviceA.getTotalCount());
            assertEquals(2, deviceA.getAvailableCount()); // 5 - 3

            assertEquals(10, deviceB.getTotalCount());
            assertEquals(5, deviceB.getAvailableCount()); // 10 - 5
        }

        @Test
        @DisplayName("2025-08-02~04 기간: 예약 모두 반영 ")
        void it_shows_all_reserved() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 8, 2, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 8, 4, 0, 0);

            // when
            List<AvailableDeviceProjection> result = deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(
                    store2.getId(), rentalStart, rentalEnd
            );

            // then
            assertEquals(1, result.size());

            AvailableDeviceProjection deviceA = result.stream()
                    .filter(r -> r.getDeviceName().equals("5G 프리미엄"))
                    .findFirst().orElseThrow();

            assertEquals(20, deviceA.getTotalCount());
            assertEquals(0, deviceA.getAvailableCount());

        }
    }

    @Nested
    @DisplayName("findAvailableDevicesByStoreId 테스트")
    class Describe_findAvailableDevicesByStoreId {

        @Test
        @DisplayName("가맹점 A에서 전체 수량과 남은 수량이 같은 상태로 모두 잘 반영한다.")
        void it_returns_total_count_left_count_At_storeA() {
            // given

            // when
            List<AvailableDeviceProjection> result = deviceReservationRepository.findAvailableDevicesByStoreId(
                    store1.getId());

            // then
            assertEquals(2, result.size());
            for (AvailableDeviceProjection projection : result) {
                assertEquals(projection.getTotalCount(), projection.getAvailableCount());
            }
        }

        @Test
        @DisplayName("가맹점 B에서 전체 수량과 남은 수량이 같은 상태로 모두 잘 반영한다.")
        void it_returns_total_count_left_count_At_storeB() {
            // given

            // when
            List<AvailableDeviceProjection> result = deviceReservationRepository.findAvailableDevicesByStoreId(
                    store2.getId());

            // then
            assertEquals(1, result.size());
            for (AvailableDeviceProjection projection : result) {
                assertEquals(projection.getTotalCount(), projection.getAvailableCount());
            }
        }


    }

    @Nested
    @DisplayName("findAvailableCountsByStoreDeviceIdAndPeriod 테스트")
    class Describe_findAvailableCountsByStoreDeviceIdAndPeriod {

        @Test
        @DisplayName("예약 기간과 겹치지 않은 경우 전체 갯수가 반환된다.")
        void it_returns_total_count_on_no_overlap() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 9, 1, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 9, 2, 0, 0);

            // when
            Optional<Long> counts = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    storeDevice1A.getId(), rentalStart, rentalEnd
            );

            // then
            Assertions.assertThat(counts.get()).isEqualTo(5);
        }

        @Test
        @DisplayName("2025-07-01~02 기간에 겹치는 경우 뺴진 경우가 반환된다.")
        void it_subtracts_first_reservation_only() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 7, 1, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 7, 2, 0, 0);

            // when
            Optional<Long> counts1 = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    storeDevice1A.getId(), rentalStart, rentalEnd
            );
            Optional<Long> counts2 = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    storeDevice1B.getId(), rentalStart, rentalEnd
            );

            // then
            Assertions.assertThat(counts1.get()).isEqualTo(2);
            Assertions.assertThat(counts2.get()).isEqualTo(5);
        }

        @Test
        @DisplayName("2025-08-02~04 기간: 예약 모두 반영 ")
        void it_shows_zero() {
            // given
            LocalDateTime rentalStart = LocalDateTime.of(2025, 8, 2, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 8, 4, 0, 0);

            // when
            Optional<Long> counts1 = deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(
                    storeDevice2C.getId(), rentalStart, rentalEnd
            );

            // then
            Assertions.assertThat(counts1.get()).isEqualTo(0);

        }
    }

}