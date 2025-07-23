    package com.TwoSeaU.BaData.domain.store.repository;

    import static org.assertj.core.api.Assertions.*;

    import com.TwoSeaU.BaData.domain.store.dto.projection.StoreWithDistanceProjection;
    import com.TwoSeaU.BaData.domain.store.entity.Store;
    import com.TwoSeaU.BaData.global.config.QueryDSLConfig;
    import java.time.LocalTime;
    import org.junit.jupiter.api.AfterAll;
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
    class StoreRepositoryTest {

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
        static void stopNeo4j() {

            postgresContainer.close();
        }

        @Autowired
        StoreRepository storeRepository;

        @Nested
        @DisplayName("가맹점과 함께 거리를 확인할 때")
        class Describe_findStoreWithDistance {

            @Test
            @DisplayName("정상적으로 조회 가능하다.")
            void it_returns_normal_response() {

                // given
                Point storeLocation = new GeometryFactory(new PrecisionModel(), 4326)
                        .createPoint(new Coordinate(127.033811720536, 37.5580590996977)); // (lon, lat)
                storeLocation.setSRID(4326);

                final Store store = Store.of( "테스트 매장",
                        storeLocation,
                        "010-1234-5678",
                        "서울시 강남구",
                        5,
                        "https://test.com/image.jpg",
                        LocalTime.of(9, 0),
                        LocalTime.of(18,0));

                storeRepository.save(store);

                double myLat = 37.560000;
                double myLon = 127.030000;

                // when
                StoreWithDistanceProjection result = storeRepository.findStoreWithDistance(store.getId(), myLat, myLon);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStoreId()).isEqualTo(store.getId());
                assertThat(result.getName()).isEqualTo("테스트 매장");
                assertThat(result.getDetailAddress()).isEqualTo("서울시 강남구");
                assertThat(result.getPhoneNumber()).isEqualTo("010-1234-5678");
                assertThat(result.getDistanceFromMe()).isGreaterThan(0.0);
            }

        }

    }