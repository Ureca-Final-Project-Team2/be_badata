package com.TwoSeaU.BaData.domain.user.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.repository.ReportRepository;
import com.TwoSeaU.BaData.domain.user.dto.response.CoinResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetDataResponse;
import com.TwoSeaU.BaData.domain.user.entity.PlanData;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.PlanDataRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PlanDataRepository planDataRepository;

	@Mock
	private SosRepository sosRepository;

	@Mock
	private ReportRepository reportRepository;

	private final Long cursor = 1L;
	private final int size = 10;

	@Nested
	@DisplayName("getDate 메서드는")
	class Describe_getData {

		@Nested
		@DisplayName("정상 흐름에서는")
		class Context_with_success {

			@Test
			@DisplayName("사용자 데이터양을 반환한다")
			void it_returns_data() {

				// given
				PlanData planData = PlanData.of("planName", 500);
				given(planDataRepository.findById(any())).willReturn(Optional.of(planData));
				given(userRepository.findByUsername(any())).willReturn(Optional.of(
					User.of("testNickname", "testUsername", "testPassword", 150, 10, Role.GENERAL, SocialType.KAKAO,
						"email", "testEmail", planData)
				));

				// when
				GetDataResponse result = userService.getData("username");

				// then
				assertThat(result.getDataAmount()).isEqualTo(150);
			}
		}

		@Nested
		@DisplayName("예외 상황에서는")
		class Context_with_failure {

			@Test
			@DisplayName("사용자가 존재하지 않으면 예외를 던진다")
			void it_throws_exception_when_user_not_found() {

				// given
				given(userRepository.findByUsername(any())).willReturn(Optional.empty());

				// when
				GeneralException exception = assertThrows(GeneralException.class, () -> {
					userService.getData("username");
				});

				// then
				assertThat(exception.getBaseException().getHttpStatus()).isEqualTo(
					UserException.USER_NOT_FOUND.getHttpStatus()
				);
			}
		}
	}

	@Nested
	@DisplayName("getCoin 메서드는")
	class Describe_getCoin {

		@Nested
		@DisplayName("정상 흐름에서는")
		class Context_with_success {

			@Test
			@DisplayName("사용자 코인수를 반환한다")
			void it_returns_coin() {

				// given
				given(userRepository.findByUsername(any())).willReturn(Optional.of(
					User.of("testNickname", "testUsername", "testPassword", 150, 10, Role.GENERAL, SocialType.KAKAO,
						"testEmail", "testProfileUrl", null)
				));

				// when
				CoinResponse result = userService.getCoin("username");

				// then
				assertThat(result.getCoin()).isEqualTo(10);
			}
		}

		@Nested
		@DisplayName("예외 상황에서는")
		class Context_with_failure {

			@Test
			@DisplayName("사용자가 존재하지 않으면 예외를 던진다")
			void it_throws_exception_when_user_not_found() {

				// given
				given(userRepository.findByUsername(any())).willReturn(Optional.empty());

				// when
				GeneralException exception = assertThrows(GeneralException.class, () -> {
					userService.getCoin("username");
				});

				// then
				assertThat(exception.getBaseException().getHttpStatus()).isEqualTo(
					UserException.USER_NOT_FOUND.getHttpStatus()
				);
			}
		}
	}

	private User createTestUser() {
		return User.of(
			"testNickName",
			"testUser",
			"encodedPassword",
			100,
			50,
			Role.GENERAL,
			SocialType.KAKAO,
			"test@example.com",
			"profileImageUrl",
			PlanData.of("기본 요금제", 1000)
		);
	}

	private Gifticon createTestGifticon(User user) {
		GifticonCategory gifticonCategory = GifticonCategory.of("CAFE");

		Gifticon gifticon = new Gifticon(
			user,
			"title",
			"comment",
			BigDecimal.valueOf(10000),
			LocalDate.now().plusDays(10),
			"http://image.url",
			false,
			"couponNumber",
			"partner",
			gifticonCategory,
			null,
			null
		);

		return gifticon;
	}
}