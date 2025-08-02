package com.TwoSeaU.BaData.domain.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.TwoSeaU.BaData.domain.rental.dto.projection.AvailableDeviceProjection;
import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveDeviceRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveRentalRequest;
import com.TwoSeaU.BaData.domain.rental.dto.response.ShowReservationDeviceInfoResponse;
import com.TwoSeaU.BaData.domain.rental.entity.Reservation;
import com.TwoSeaU.BaData.domain.rental.exception.RentalException;
import com.TwoSeaU.BaData.domain.rental.repository.DeviceReservationRepository;
import com.TwoSeaU.BaData.domain.rental.repository.ReservationRepository;
import com.TwoSeaU.BaData.domain.store.entity.Device;
import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreDevice;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreDeviceRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @InjectMocks
    RentalService rentalService;

    @Mock
    StoreRepository storeRepository;

    @Mock
    DeviceReservationRepository deviceReservationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    StoreDeviceRepository storeDeviceRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Nested
    @DisplayName("getReservationDeviceInfoResponse 메서드는")
    class Describe_getReservationDeviceInfoResponse {

        final Long storeId = 1L;

        @Test
        @DisplayName("존재하지 않는 가맹점이면 예외를 던진다")
        void it_throws_if_store_not_exists() {
            // given
            given(storeRepository.existsById(storeId)).willReturn(false);

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, () -> rentalService.getReservationDeviceInfoResponse(
                    LocalDateTime.now(), LocalDateTime.now(), storeId));

            //then
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(ex.getBaseException().getMessage()).isEqualTo(StoreException.CANT_FIND_STORE.getMessage());

        }

        @Test
        @DisplayName("rentalStartDate, rentalEndDate가 null이면 전체 기기 목록을 반환한다")
        void it_returns_all_devices_if_period_null() {
            // given
            given(storeRepository.existsById(storeId)).willReturn(true);

            AvailableDeviceProjection mockProjection = mock(AvailableDeviceProjection.class);
            given(mockProjection.getDeviceId()).willReturn(1L);
            given(mockProjection.getDeviceName()).willReturn("LTE 기본형");

            given(deviceReservationRepository.findAvailableDevicesByStoreId(storeId))
                    .willReturn(List.of(mockProjection));

            // when
            List<ShowReservationDeviceInfoResponse> result =
                    rentalService.getReservationDeviceInfoResponse(null, null, storeId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDeviceId()).isEqualTo(1L);
            verify(deviceReservationRepository, times(1)).findAvailableDevicesByStoreId(storeId);
        }

        @Test
        @DisplayName("대여 기간이 주어지면 해당 기간 기준으로 가용 기기 목록을 반환한다")
        void it_returns_available_devices_by_period() {
            // given
            given(storeRepository.existsById(storeId)).willReturn(true);

            LocalDateTime rentalStart = LocalDateTime.of(2025, 7, 1, 0, 0);
            LocalDateTime rentalEnd = LocalDateTime.of(2025, 7, 2, 0, 0);

            AvailableDeviceProjection mockProjection = mock(AvailableDeviceProjection.class);
            given(mockProjection.getDeviceId()).willReturn(2L);
            given(mockProjection.getDeviceName()).willReturn("5G 무제한 기기");

            given(deviceReservationRepository.findAvailableDevicesByStoreIdAndPeriod(storeId, rentalStart, rentalEnd))
                    .willReturn(List.of(mockProjection));

            // when
            List<ShowReservationDeviceInfoResponse> result =
                    rentalService.getReservationDeviceInfoResponse(rentalStart, rentalEnd, storeId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDeviceId()).isEqualTo(2L);
            verify(deviceReservationRepository, times(1)).findAvailableDevicesByStoreIdAndPeriod(storeId,rentalStart,rentalEnd);
        }
    }



    @Nested
    @DisplayName("reserveRental 메서드는")
    class Describe_reserveRental {
        private final Long storeId = 1L;
        private final String username = "kakao1234";
        private final LocalDateTime start = LocalDateTime.of(2026, 7, 10, 10, 0);
        private final LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        private final User user = User.of(username, "id", "pw", 10, 10, Role.GENERAL, SocialType.KAKAO, "email", "img", null);
        private final Store store = Store.of("가맹점", null, "02", "주소", 10, "img", LocalTime.of(9, 0), LocalTime.of(21, 0));
        private final StoreDevice storeDevice = StoreDevice.of(store, Device.of("5G", 1000, 5, true, "img"), 10, 5000, 10);

        @Test
        @DisplayName("정상 예약 시 reservationId를 반환한다")
        void it_returns_reservation_id_when_success() throws Exception {
            // given
            ReserveDeviceRequest deviceRequest = ReserveDeviceRequest.builder()
                    .storeDeviceId(10L)
                    .count(2)
                    .build();

            ReserveRentalRequest rentalRequest =ReserveRentalRequest.builder().storeDevices(List.of(deviceRequest))
                            .rentalStartDate(start)
                                    .rentalEndDate(end)
                                            .storeId(storeId)
                                                    .build();

            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(storeDeviceRepository.findById(10L)).willReturn(Optional.of(storeDevice));
            given(deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(anyLong(), any(), any()))
                    .willReturn(Optional.of(5L));
            Field storeIdField = Store.class.getDeclaredField("id");
            storeIdField.setAccessible(true);
            storeIdField.set(store, 1L);  // 예시
            Field storeDeviceIdField = StoreDevice.class.getDeclaredField("id");
            storeDeviceIdField.setAccessible(true);
            storeDeviceIdField.set(storeDevice, 10L);  // 예: 10L

            given(reservationRepository.save(any())).willAnswer(invocation -> {
                Reservation r = invocation.getArgument(0);

                Field reservationIdField = Reservation.class.getDeclaredField("id");
                reservationIdField.setAccessible(true);
                reservationIdField.set(r, 100L);

                return r;
            });

            // when
            Long result = rentalService.reserveRental(rentalRequest, username);

            // then
            assertThat(result).isEqualTo(100L);
            verify(reservationRepository, times(1)).save(any());
            verify(deviceReservationRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("storeDevice의 storeId가 요청과 다르면 예외를 던진다")
        void it_throws_when_store_device_store_mismatch()
                throws NoSuchFieldException, IllegalAccessException {
            Store otherStore = Store.of("다른 지점", null, "02", "주소", 10, "img", LocalTime.of(9, 0), LocalTime.of(21, 0));

            // 리플렉션으로 다른 ID 부여
            Field storeIdField = Store.class.getDeclaredField("id");
            storeIdField.setAccessible(true);
            storeIdField.set(otherStore, 99L); // storeId와 다르게 설정

            StoreDevice mismatchedDevice = StoreDevice.of(otherStore, storeDevice.getDevice(), 10, 5000, 10);
            Field idField = StoreDevice.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mismatchedDevice, 2L);

            ReserveDeviceRequest deviceRequest = ReserveDeviceRequest.builder()
                    .storeDeviceId(2L)
                    .count(1)
                    .build();

            ReserveRentalRequest rentalRequest =ReserveRentalRequest.builder().storeDevices(List.of(deviceRequest))
                    .rentalStartDate(start)
                    .rentalEndDate(end)
                    .storeId(1L)
                    .build();

            given(storeDeviceRepository.findById(2L)).willReturn(Optional.of(mismatchedDevice));

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, () -> rentalService.reserveRental(rentalRequest, username));

            //then
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ex.getBaseException().getMessage()).isEqualTo(RentalException.DONT_MATCH_STORE_DEVICE_STORE.getMessage());
        }

        @Test
        @DisplayName("예약 수량이 보유 수량보다 많으면 예외를 던진다")
        void it_throws_when_reservation_exceeds_device_count() throws Exception{
            // given
            ReserveDeviceRequest deviceRequest = ReserveDeviceRequest.builder()
                    .storeDeviceId(10L)
                    .count(11)
                    .build();

            ReserveRentalRequest rentalRequest =ReserveRentalRequest.builder().storeDevices(List.of(deviceRequest))
                    .rentalStartDate(start)
                    .rentalEndDate(end)
                    .storeId(storeId)
                    .build();

            given(storeDeviceRepository.findById(10L)).willReturn(Optional.of(storeDevice));
            Field storeIdField = Store.class.getDeclaredField("id");
            storeIdField.setAccessible(true);
            storeIdField.set(store, 1L);  // 예시

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, () -> rentalService.reserveRental(rentalRequest, username));

            //then
            assertThat(ex.getBaseException().getMessage()).isEqualTo(RentalException.CANT_RESERVATION_MORE_THAN_COUNT.getMessage());
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ex.getBaseException().getMessage()).isEqualTo(RentalException.CANT_RESERVATION_MORE_THAN_COUNT.getMessage());
        }

        @Test
        @DisplayName("동일 기간에 이미 예약된 수량이 남은 수량보다 많으면 예외를 던진다")
        void it_throws_when_already_reserved_same_period() throws Exception {
            // given
            ReserveDeviceRequest deviceRequest = ReserveDeviceRequest.builder()
                    .storeDeviceId(10L)
                    .count(5)
                    .build();

            ReserveRentalRequest rentalRequest =ReserveRentalRequest.builder().storeDevices(List.of(deviceRequest))
                    .rentalStartDate(start)
                    .rentalEndDate(end)
                    .storeId(storeId)
                    .build();

            Field storeIdField = Store.class.getDeclaredField("id");
            storeIdField.setAccessible(true);
            storeIdField.set(store, 1L);  // 예시
            Field storeDeviceIdField = StoreDevice.class.getDeclaredField("id");
            storeDeviceIdField.setAccessible(true);
            storeDeviceIdField.set(storeDevice, 10L);  // 예시

            given(storeDeviceRepository.findById(storeDevice.getId())).willReturn(Optional.of(storeDevice));
            given(deviceReservationRepository.findAvailableCountsByStoreDeviceIdAndPeriod(anyLong(), any(), any()))
                    .willReturn(Optional.of(2L)); // 예약 불가

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, () -> rentalService.reserveRental(rentalRequest, username));

            //then
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ex.getBaseException().getMessage()).isEqualTo(RentalException.ALREADY_RENTAL_EXIST_SAME_PERIOD.getMessage());
        }
    }

    @Nested
    @DisplayName("getReservedDeviceByReservationId 메서드는")
    class Describe_getReservedDeviceByReservationId {

        final Long reservationId = 1L;
        final String username = "kakao1234";
        final Long userId = 10L;

        private final Store store = Store.of("서울 강남점", null, "02-123", "주소", 10, "img", LocalTime.of(9, 0), LocalTime.of(21, 0));
        private final User user = User.of(username, "socialId", "pw", 10, 10, Role.GENERAL, SocialType.KAKAO, "email", "img", null);

        @Test
        @DisplayName("예약이 존재하지 않으면 예외를 던진다")
        void it_throws_when_reservation_not_found() {

            // given
            given(reservationRepository.findById(any())).willReturn(Optional.empty());

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, ()-> rentalService.getReservedDeviceByReservationId(reservationId, username));

            //then
            assertThat(ex.getBaseException().getMessage()).isEqualTo(RentalException.RESERVATION_NOT_FOUND.getMessage());
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외를 던진다")
        void it_throws_when_user_not_found() {
            // given
            Reservation reservation = mock(Reservation.class);
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            //when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, ()-> rentalService.getReservedDeviceByReservationId(reservationId, username));

            //then
            assertThat(ex.getBaseException().getMessage()).isEqualTo(UserException.USER_NOT_FOUND.getMessage());
            assertThat(ex.getBaseException().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("로그인한 유저가 예약의 주인이 아니라면 예외를 던진다")
        void it_throws_when_accessing_other_users_reservation() {
            // given
            Reservation reservation = mock(Reservation.class);
            User loginUser = mock(User.class); // 로그인한 유저
            User owner = mock(User.class);     // 예약 주인 (다른 유저)

            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
            given(userRepository.findByUsername(username)).willReturn(Optional.of(loginUser));

            given(reservation.getUser()).willReturn(owner);
            given(owner.getId()).willReturn(99L);     // 실제 예약 주인의 ID
            given(loginUser.getId()).willReturn(10L); // 로그인한 유저의 ID

            // when
            GeneralException ex = org.junit.jupiter.api.Assertions.assertThrows(GeneralException.class, ()-> rentalService.getReservedDeviceByReservationId(reservationId, username));

            // then
            assertThat(ex.getBaseException().getMessage())
                    .isEqualTo(RentalException.CANT_ACCESS_TO_OTHER_RESERVATION.getMessage());
            assertThat(ex.getBaseException().getHttpStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }


    }


}