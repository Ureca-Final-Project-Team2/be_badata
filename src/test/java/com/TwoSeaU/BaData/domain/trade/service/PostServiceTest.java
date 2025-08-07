package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.SuspiciousRegion;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.DeletePostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.MobileCarrier;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector.PostVectorizerDouble;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.PostVectorizerFloat;
import com.TwoSeaU.BaData.domain.user.entity.PlanData;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.Role;
import com.TwoSeaU.BaData.domain.user.enums.SocialType;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    GifticonRepository gifticonRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    GifticonCategoryRepository gifticonCategoryRepository;
    @Mock
    DataRepository dataRepository;
    @Mock
    ELAService elaService;
    @Mock
    S3ImageService s3ImageService;
    @Mock
    PostRepository postRepository;
    @Mock
    PostVectorizerDouble postVectorizerDouble;
    @Mock
    PostVectorizerFloat postVectorizerFloat;
    @Mock
    PostDocumentRepository postDocumentRepository;
    @Mock
    JdbcRepository jdbcRepository;


    private static final String USERNAME = "testUser";
    private static final String IMAGE_URL = "https://s3.amazonaws.com/trades/gifticon/test.jpg";
    private static final String CATEGORY_NAME = "OTT/뮤직";

    @Nested
    @DisplayName("deletePost 메서드는")
    class Describe_deleteGifticonPost {

        @Nested
        @DisplayName("정상 흐름(해피 케이스)에서는")
        class Context_with_success {

            @Test
            @DisplayName("게시물을 삭제한다.")
            void it_deletes_gifticon_post() {
                // given
                Long userId = 1L;
                User user = createTestUser();
                Post post = createTestPost(user);

                ReflectionTestUtils.setField(user, "id", userId);
                given(userRepository.findByUsername(post.getSeller().getUsername())).willReturn(Optional.of(user));
                given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

                // when
                DeletePostResponse response = postService.deletePost(post.getId(), USERNAME);

                // then
                assertThat(response.getPostId()).isEqualTo(post.getId());
            }
        }

        @Nested
        @DisplayName("예외 상황에서는")
        class Context_with_failure {

            @Test
            @DisplayName("판매자가 아니라면 POST_ACCESS_DENIED 예외를 던진다.")
            void it_throws_exception_when_user_is_not_seller() {
                // given
                Long userId = 1L;
                Long anotherUserId = 2L;
                String anotherUsername = "notFoundUser";
                User user = User.of(
                        "anotherNickName",
                        anotherUsername,
                        "encodedPassword",
                        100,
                        50,
                        Role.ADMIN,
                        SocialType.KAKAO,
                        "antoherTest@example.com",
                        "https://example.com/profile.jpg",
                        PlanData.of("기본 요금제", 1000)
                );

                User seller = createTestUser();
                Post post = createTestPost(seller);

                ReflectionTestUtils.setField(user, "id", anotherUserId);
                ReflectionTestUtils.setField(seller, "id", userId);
                given(userRepository.findByUsername(anotherUsername)).willReturn(Optional.of(user));
                given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.deletePost(post.getId(), anotherUsername);
                });
                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        TradeException.POST_ACCESS_DENIED.getMessage());
            }

            @Test
            @DisplayName("이미 판매되었다면 SOLD_POST_ALREADY 예외를 던진다.")
            void it_throws_exception_when_is_sold() {
                Long userId = 1L;
                User user = createTestUser();
                Post post = createTestPost(user);

                ReflectionTestUtils.setField(user, "id", userId);
                given(userRepository.findByUsername(post.getSeller().getUsername())).willReturn(Optional.of(user));
                given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
                given(post.getIsSold()).willReturn(true);

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.deletePost(post.getId(), USERNAME);
                });
                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        TradeException.SOLD_POST_ALREADY.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("createGifticonPost 메서드는")
    class Describe_createGifticonPost {

        @Nested
        @DisplayName("정상 흐름(해피 케이스)에서는")
        class Context_with_success {

            @Test
            @DisplayName("기프티콘 게시물을 생성한다.")
            void it_creates_gifticon_post() {
                    // given
                    MultipartFile mockFile = createMockFile();
                    SaveGifticonPostRequest request = createSaveGifticonPostRequest(mockFile);

                    User user = createTestUser();
                    GifticonCategory category = GifticonCategory.of(CATEGORY_NAME);

                    ELAResult elaResult = new ELAResult(List.of(), 0.0, 0.0);
                    Gifticon savedGifticon = mock(Gifticon.class);
                    given(savedGifticon.getId()).willReturn(1L);

                    given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
                    given(gifticonCategoryRepository.findByCategoryName(CATEGORY_NAME)).willReturn(Optional.of(category));
                    given(elaService.analyzeImage(mockFile)).willReturn(elaResult);
                    given(s3ImageService.saveImage(eq(mockFile), anyString(), anyString())).willReturn(IMAGE_URL);
                    given(gifticonRepository.save(any(Gifticon.class))).willReturn(savedGifticon);

                    // when
                    SavePostResponse response = postService.createGifticonPost(request, USERNAME);

                    // then
                    assertThat(response.getPostId()).isEqualTo(1L);
                }
            }

        @Nested
        @DisplayName("예외 상황에서는")
        class Context_with_failure {
            @Test
            @DisplayName("중복된 코드 번호라면 DUPLICATE_COUPON_NUMBER 예외를 던진다.")
            void it_throws_exception_when_coupon_number_is_duplicate() {
                // given
                User user = createTestUser();
                MultipartFile mockFile = createMockFile();

                SaveGifticonPostRequest request = createSaveDuplicateGifticonPostRequest(mockFile);

                given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
                given(gifticonRepository.existsByCouponNumber(request.getCouponNumber())).willReturn(true);

                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.createGifticonPost(request, USERNAME);
                });

                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        TradeException.DUPLICATE_COUPON_NUMBER.getMessage());
            }

            @Test
            @DisplayName("사용자를 찾을 수 없으면 USER_NOT_FOUND 예외를 던진다.")
            void it_throws_exception_when_user_not_found() {
                // given
                String username = "notFoundUser";
                MultipartFile mockFile = createMockFile();
                SaveGifticonPostRequest request = createSaveGifticonPostRequest(mockFile);
                given(userRepository.findByUsername(username)).willReturn(Optional.empty());

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.createGifticonPost(request, username);
                });

                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        UserException.USER_NOT_FOUND.getMessage());
            }

            @Test
            @DisplayName("조작된 이미지 감지 시 SUSPICIOUS_IMAGE_DETECTED 예외를 던진다.")
            void it_throws_exception_when_manipulated_image_detected() {
                // given
                User user = createTestUser();
                GifticonCategory category = createTestGifticonCategory();
                MultipartFile mockFile = createMockFile();

                SaveGifticonPostRequest request = SaveGifticonPostRequest.builder()
                        .category(CATEGORY_NAME)
                        .file(mockFile)
                        .build();

                given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
                given(gifticonCategoryRepository.findByCategoryName(CATEGORY_NAME)).willReturn(Optional.of(category));
                List<SuspiciousRegion> regions = List.of(
                        SuspiciousRegion.of(10, 20, 30, 40, 1.0)
                );
                ELAResult elaResult = new ELAResult(regions, 0.5, 99.0);
                given(elaService.analyzeImage(any())).willReturn(elaResult);

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.createGifticonPost(request, USERNAME);
                });

                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        TradeException.SUSPICIOUS_IMAGE_DETECTED.getMessage());
            }

            @Test
            @DisplayName("카테고리를 찾을 수 없으면 NOT_FOUND_GIFTICON_CATEGORY 예외를 던진다.")
            void it_throws_exception_when_category_not_found () {
                // given
                String categoryName = "NonExistentCategory";
                User user = createTestUser();
                MultipartFile file = createMockFile();

                SaveGifticonPostRequest request = SaveGifticonPostRequest.builder()
                        .category(categoryName)
                        .file(file)
                        .build();

                given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
                given(gifticonCategoryRepository.findByCategoryName(categoryName)).willReturn(Optional.empty());

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.createGifticonPost(request, USERNAME);
                });
                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        TradeException.NOT_FOUND_GIFTICON_CATEGORY.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("createDataPost 메서드는")
    class Describe_createDataPost {

        @Nested
        @DisplayName("정상 흐름(해피 케이스)에서는")
        class Context_with_success {

            @Test
            @DisplayName("데이터 게시물을 생성한다.")
            void it_creates_gifticon_post() {
                // given
                User user = createTestUser();
                SaveDataPostRequest request = createSaveDataPostRequest();

                given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
                Data saveData = mock(Data.class);
                given(saveData.getId()).willReturn(1L);
                given(dataRepository.save(any(Data.class))).willReturn(saveData);

                // when
                SavePostResponse response = postService.createDataPost(request, USERNAME);

                // then
                assertThat(response.getPostId()).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("예외 상황에서는")
        class Context_with_failure {
            @Test
            @DisplayName("사용자를 찾을 수 없으면 USER_NOT_FOUND 예외를 던진다.")
            void it_throws_exception_when_user_not_found() {
                // given
                String username = "notFoundUser";
                SaveDataPostRequest request = createSaveDataPostRequest();
                given(userRepository.findByUsername(username)).willReturn(Optional.empty());

                // when & then
                GeneralException ex = assertThrows(GeneralException.class, () -> {
                    postService.createDataPost(request, username);
                });

                assertThat(ex.getBaseException().getMessage()).isEqualTo(
                        UserException.USER_NOT_FOUND.getMessage());
            }
        }
    }

    private GifticonCategory createTestGifticonCategory() {
        return mock(GifticonCategory.class);
    }

    private SaveGifticonPostRequest createSaveDuplicateGifticonPostRequest(MultipartFile file) {
        SaveGifticonPostRequest request = mock(SaveGifticonPostRequest.class);
        given(request.getTitle()).willReturn("테스트 기프티콘2");
        given(request.getComment()).willReturn("테스트 설명2");
        given(request.getPrice()).willReturn(BigDecimal.valueOf(12000));
        given(request.getDeadLine()).willReturn(LocalDate.now().plusDays(4));
        given(request.getCategory()).willReturn("OTT/뮤직");
        given(request.getCouponNumber()).willReturn("1234567890");
        given(request.getPartner()).willReturn("스타벅스");
        given(request.getFile()).willReturn(file);
        return request;
    }

    private SaveGifticonPostRequest createSaveGifticonPostRequest(MultipartFile file) {
        SaveGifticonPostRequest request = mock(SaveGifticonPostRequest.class);
        given(request.getTitle()).willReturn("테스트 기프티콘");
        given(request.getComment()).willReturn("테스트 설명");
        given(request.getPrice()).willReturn(BigDecimal.valueOf(1000));
        given(request.getDeadLine()).willReturn(LocalDate.now().plusDays(7));
        given(request.getCategory()).willReturn("OTT/뮤직");
        given(request.getCouponNumber()).willReturn("1234567890");
        given(request.getPartner()).willReturn("스타벅스");
        given(request.getFile()).willReturn(file);
        return request;
    }

    private SaveDataPostRequest createSaveDataPostRequest() {
        SaveDataPostRequest request = mock(SaveDataPostRequest.class);
        given(request.getTitle()).willReturn("테스트 데이터");
        given(request.getComment()).willReturn("테스트 설명");
        given(request.getPrice()).willReturn(BigDecimal.valueOf(1000));
        given(request.getDeadLine()).willReturn(LocalDate.now().plusDays(7));
        given(request.getMobileCarrier()).willReturn(MobileCarrier.SKT);
        given(request.getCapacity()).willReturn(64);
        return request;
    }

    private User createTestUser() {
        return User.of(
                "testNickName",
                "testUser",
                "encodedPassword",
                100,
                50,
                Role.ADMIN,
                SocialType.KAKAO,
                "test@example.com",
                "https://example.com/profile.jpg",
                PlanData.of("기본 요금제", 1000)
        );
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

    private MultipartFile createMockFile() {
        return new MockMultipartFile("file", "test.jpg", "image/jpeg", "image data".getBytes());
    }
}