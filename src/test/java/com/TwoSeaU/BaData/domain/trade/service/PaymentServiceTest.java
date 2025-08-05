package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.request.GetMerchantUidRequest;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PaymentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostDocumentRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.PlanData;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.repository.CoinHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PostDocumentRepository postDocumentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CoinHistoryRepository coinHistoryRepository;

    @Mock
    private IamportClient iamportClient;

    @Nested
    @DisplayName("createOrder 메서드는")
    class Describe_createOrder {
        Long postId = 1L;
        User seller = User.of(
                "sellerNickName",
                "sellerUser",
                "encodedPassword",
                100,
                50,
                Role.ADMIN,
                SocialType.KAKAO,
                "seller@example.com",
                "https://example.com/profile.jpg",
                PlanData.of("기본 요금제", 1000)
        );

        Post post = createTestPost(seller);

        User buyer = User.of(
                "buyerNickName",
                "buyer",
                "encodedPassword",
                100,
                50,
                Role.ADMIN,
                SocialType.KAKAO,
                "buyer@example.com",
                "https://example.com/profile.jpg",
                PlanData.of("기본 요금제", 1000)
        );

        @Test
        @DisplayName("게시글이 지워진 경우 DELETED_POST_ACCESS_DENIED 예외를 발생시킨다.")
        void it_throws_exception_when_post_deleted() {
            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(100))
                    .build();

            given(userRepository.findByUsername(buyer.getUsername())).willReturn(Optional.of(buyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));
            given(post.getIsDeleted()).willReturn(true);

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, buyer.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.DELETED_POST_ACCESS_DENIED.getMessage());
        }

        @Test
        @DisplayName("게시글이 팔린 경우 PAYMENT_DUPLICATE 예외를 발생시킨다.")
        void it_throws_exception_when_post_is_sold() {
            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(100))
                    .build();

            given(userRepository.findByUsername(buyer.getUsername())).willReturn(Optional.of(buyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));
            given(post.getIsSold()).willReturn(true);

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, buyer.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.PAYMENT_DUPLICATE.getMessage());
        }

        @Test
        @DisplayName("본인 게시글을 구매하려고 할 경우 SELF_PAYMENT_DENIED 예외를 발생시킨다.")
        void it_throws_exception_when_self_pay() {
            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(100))
                    .build();

            given(userRepository.findByUsername(seller.getUsername())).willReturn(Optional.of(seller));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, seller.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.SELF_PAYMENT_DENIED.getMessage());
        }

        @Test
        @DisplayName("코인이 부족할 경우 COIN_NOT_ENOUGH 예외를 발생시킨다.")
        void it_throws_exception_when_coin_not_enough() {
            ReflectionTestUtils.setField(seller, "id", 1L);
            ReflectionTestUtils.setField(buyer, "id", 2L);

            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(1000))
                    .build();

            given(userRepository.findByUsername(buyer.getUsername())).willReturn(Optional.of(buyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, buyer.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.COIN_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("코인을 소수점 단위로 사용할 경우 COIN_DECIMAL_NOT_ALLOWED 예외를 발생시킨다.")
        void it_throws_exception_when_use_coin_decimal() {
            ReflectionTestUtils.setField(seller, "id", 1L);
            ReflectionTestUtils.setField(buyer, "id", 2L);

            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(10.123))
                    .build();

            given(userRepository.findByUsername(buyer.getUsername())).willReturn(Optional.of(buyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, buyer.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.COIN_DECIMAL_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("코인을 가격보다 많이 사용할 경우 COIN_EXCEED_PRICE 예외를 발생시킨다.")
        void it_throws_exception_when_use_coin_minus() {
            ReflectionTestUtils.setField(seller, "id", 1L);
            ReflectionTestUtils.setField(buyer, "id", 2L);

            User anotherBuyer = User.of(
                    "buyerNickName",
                    "buyer",
                    "encodedPassword",
                    100,
                    100000,
                    Role.ADMIN,
                    SocialType.KAKAO,
                    "buyer@example.com",
                    "https://example.com/profile.jpg",
                    PlanData.of("기본 요금제", 1000)
            );

            //given
            GetMerchantUidRequest request = GetMerchantUidRequest.builder()
                    .useCoin(BigDecimal.valueOf(10000))
                    .build();

            given(userRepository.findByUsername(anotherBuyer.getUsername())).willReturn(Optional.of(anotherBuyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.createOrder(postId, anotherBuyer.getUsername(), request);
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.COIN_EXCEED_PRICE.getMessage());
        }
    }

    @Nested
    @DisplayName("validateIamport 메서드는")
    class Describe_validateIamport {
        Long postId = 1L;
        String impUid = "imp_1234";
        String merchantUid = "20250804abc";
        User buyer = User.of(
                "buyerNickName",
                "buyer",
                "encodedPassword",
                100,
                50,
                Role.ADMIN,
                SocialType.KAKAO,
                "buyer@example.com",
                "https://example.com/profile.jpg",
                PlanData.of("기본 요금제", 1000)
        );
        Post post = createTestPost(buyer);

        @Test
        @DisplayName("정상적으로 결제되지 않은 경우 PAYMENT_FAILED 예외를 발생시킨다.")
        void it_throws_exception_when_not_paid() throws IamportResponseException, IOException {
            //given
            createPortOneResponse(impUid, "failed", BigDecimal.valueOf(100));

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () -> {
                paymentService.validateIamport(impUid, postId, buyer.getUsername());
            });

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.PAYMENT_FAILED.getMessage());
        }

        @Test
        @DisplayName("결제 시도 내역을 찾을 수 없는 경우 PAYMENT_NOT_FOUND 예외를 발생시킨다.")
        void it_throws_exception_when_payment_not_found() throws IamportResponseException, IOException {
            //given
            createPortOneResponse(impUid, "paid", BigDecimal.valueOf(100));
            given(userRepository.findByUsername(buyer.getUsername())).willReturn(Optional.of(buyer));
            given(postRepository.findByIdWithLock(postId)).willReturn(Optional.of(post));
            given(paymentRepository.findByMerchantUid(merchantUid)).willReturn(Optional.empty());

            //when & then
            GeneralException ex = assertThrows(GeneralException.class, () ->
                paymentService.validateIamport(impUid, postId, buyer.getUsername())
            );

            assertThat(ex.getBaseException().getMessage()).isEqualTo(
                    TradeException.PAYMENT_NOT_FOUND.getMessage());
        }
    }

    private Post createTestPost(User user) {
        Post post = mock(Post.class);
        given(post.getId()).willReturn(1L);
        given(post.getSeller()).willReturn(user);
        given(post.getTitle()).willReturn("테스트 게시물");
        given(post.getComment()).willReturn("테스트 설명");
        given(post.getPrice()).willReturn(BigDecimal.valueOf(1000));
        given(post.getDeadLine()).willReturn(LocalDate.now().plusDays(7));
        given(post.getIsDeleted()).willReturn(false);
        given(post.getIsSold()).willReturn(false);
        return post;
    }

    private IamportResponse<com.siot.IamportRestClient.response.Payment> createPortOneResponse(String impUid, String status, BigDecimal amount) throws IamportResponseException, IOException {
        com.siot.IamportRestClient.response.Payment portonePayment = mock(com.siot.IamportRestClient.response.Payment.class);
        IamportResponse<com.siot.IamportRestClient.response.Payment> portOneResponse = mock(IamportResponse.class);

        given(iamportClient.paymentByImpUid(impUid)).willReturn(portOneResponse);
        given(portOneResponse.getResponse()).willReturn(portonePayment);
        given(portonePayment.getImpUid()).willReturn(impUid);
        given(portonePayment.getStatus()).willReturn(status);
        given(portonePayment.getAmount()).willReturn(amount);

        return portOneResponse;
    }
}
