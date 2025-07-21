package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.SuspiciousRegion;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonCategoryRepository;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    ELAService elaService;
    @Mock
    S3ImageService s3ImageService;

    private static final String USERNAME = "testUser";
    private static final String IMAGE_URL = "https://s3.amazonaws.com/trades/gifticon/test.jpg";
    private static final String CATEGORY_NAME = "OTT/뮤직";

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

                    ELAResult elaResult = new ELAResult(List.of(), 0);
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
                ELAResult elaResult = new ELAResult(regions, 0.85);
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

    private GifticonCategory createTestGifticonCategory() {
        return mock(GifticonCategory.class);
    }

    private SaveGifticonPostRequest createSaveGifticonPostRequest(MultipartFile file) {
        SaveGifticonPostRequest request = mock(SaveGifticonPostRequest.class);
        given(request.getTitle()).willReturn("테스트 기프티콘");
        given(request.getComment()).willReturn("테스트 설명");
        given(request.getPrice()).willReturn(10000);
        given(request.getDeadLine()).willReturn(LocalDate.now().plusDays(7));
        given(request.getCategory()).willReturn("OTT/뮤직");
        given(request.getIssueDate()).willReturn(LocalDateTime.now());
        given(request.getCouponNumber()).willReturn("1234567890");
        given(request.getPartner()).willReturn("스타벅스");
        given(request.getFile()).willReturn(file);
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
                "https://example.com/profile.jpg"
        );
    }

    private MultipartFile createMockFile() {
        return new MockMultipartFile("file", "test.jpg", "image/jpeg", "image data".getBytes());
    }
}