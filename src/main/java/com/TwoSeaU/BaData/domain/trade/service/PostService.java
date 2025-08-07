package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.OCRResult;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.entity.*;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector.PostVectorizerDouble;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.PostVectorizerFloat;
import com.TwoSeaU.BaData.domain.trade.service.recommend.pgVector.VectorUtilsPg;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final TrendingPostService trendingPostService;
    private final PostRepository postRepository;
    private final GifticonRepository gifticonRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;
    private final PostLikesRepository postLikesRepository;
    private final PaymentRepository paymentRepository;
    private final ELAService elaService;
    private final PostVectorizerDouble postVectorizerDouble;
    private final PostVectorizerFloat postVectorizerFloat;
    private final VectorUtilsPg vectorUtilsPg;
    private final S3ImageService s3ImageService;
    private final OCRService ocrService;
    private final PostDocumentRepository postDocumentRepository;
    private final JdbcRepository jdbcRepository;
    private final PartnerRepository partnerRepository;

    private static final String COOKIE_NAME = "View_Post";

    public GetImageUploadResponse analyzeImage(final MultipartFile file){
        ELAResult elaResult = elaService.analyzeImage(file);
        OCRResult ocrResult = ocrService.extractTextFromImageFile(file);

        return GetImageUploadResponse.from(elaResult, ocrResult);
    }

    public CursorPageResponse<PostResponse> getPostsByUserId(final Long userId, final boolean isSold, final String username, final Long cursor, final int size) {

        return postRepository.searchPostsByUserAndIsSold(userId, isSold, username, cursor, size);
    }


    public CursorPageResponse<PostResponse> getPostsByDeadLine(final String username, final Long cursor, final int size) {

        return postRepository.searchPostsByDeadLine(username, cursor, size);
    }

    public SavePostResponse createGifticonPost(final SaveGifticonPostRequest saveGifticonPostRequest, final String username) {

        if(gifticonRepository.existsByCouponNumber(saveGifticonPostRequest.getCouponNumber())) {
            throw new GeneralException(TradeException.DUPLICATE_COUPON_NUMBER);
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
        final GifticonCategory category = gifticonCategoryRepository.findByCategoryName(saveGifticonPostRequest.getCategory())
                .orElseThrow(() -> new GeneralException(TradeException.NOT_FOUND_GIFTICON_CATEGORY));

        final ELAResult result = elaService.analyzeImage(saveGifticonPostRequest.getFile());

        if (result.isManipulated()) {
            throw new GeneralException(TradeException.SUSPICIOUS_IMAGE_DETECTED);
        }

        final String imageUrl = s3ImageService.saveImage(saveGifticonPostRequest.getFile(), "trades/gifticon/", saveGifticonPostRequest.getFile().getOriginalFilename());

        final double[] doubleVector = postVectorizerDouble.vectorizePost(
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                category,
                saveGifticonPostRequest.getPartner()
        );

        final float[] floatVector = postVectorizerFloat.vectorizePost(
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                category,
                saveGifticonPostRequest.getPartner()
        );

        final Gifticon gifticon = new Gifticon(
                user,
                saveGifticonPostRequest.getTitle(),
                saveGifticonPostRequest.getComment(),
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                imageUrl,
                false,
                saveGifticonPostRequest.getCouponNumber(),
                saveGifticonPostRequest.getPartner(),
                category,
                doubleVector,
                floatVector
                //floatVector
                //byteVector
        );

        final Gifticon savedGifticon = gifticonRepository.save(gifticon);
        final PostDocument postDocument = PostDocument.from(savedGifticon);
        postDocumentRepository.save(postDocument);

        //TODO: 여기
        jdbcRepository.insertPostVector(savedGifticon.getId(), floatVector);

        return SavePostResponse.builder()
                .postId(savedGifticon.getId())
                .build();
    }


    public SavePostResponse createDataPost(final SaveDataPostRequest saveDataPostRequest, final String username) {

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Data data = new Data(
                user,
                saveDataPostRequest.getTitle(),
                saveDataPostRequest.getComment(),
                saveDataPostRequest.getPrice(),
                saveDataPostRequest.getDeadLine(),
                null,
                false,
                saveDataPostRequest.getMobileCarrier(),
                saveDataPostRequest.getCapacity()
        );

        final Data savedData = dataRepository.save(data);
        final PostDocument postDocument = PostDocument.from(savedData);
        postDocumentRepository.save(postDocument);

        return SavePostResponse.builder()
                .postId(savedData.getId())
                .build();
    }

    private String getCookieValue(final Long postId) {
        return "[" + postId + "]";
    }

    private void setCookieAndRecordView(final Long postId, final HttpServletRequest request, final HttpServletResponse response){
        final Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    if (!cookie.getValue().contains(getCookieValue(postId))) {
                        cookie.setValue(cookie.getValue() + getCookieValue(postId));
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        response.addCookie(cookie);

                        trendingPostService.recordView(postId);
                    }

                    return;
                }
            }
        }

        final Cookie newCookie = new Cookie(COOKIE_NAME, getCookieValue(postId));
        newCookie.setPath("/");
        newCookie.setHttpOnly(true);
        response.addCookie(newCookie);

        trendingPostService.recordView(postId);
    }

    public GetPostDetailResponse getPost(final Long postId, final String username, final HttpServletRequest request, final HttpServletResponse response) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold() && username != null) {
            final User loginUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

            if (!post.getSeller().getId().equals(loginUser.getId()) && paymentRepository.findByUserIdAndPostIdAndPaymentStatus(loginUser.getId(), postId, PaymentStatus.PAID).isEmpty()) {
                throw new GeneralException(TradeException.POST_ACCESS_DENIED);
            }
        }

        setCookieAndRecordView(postId, request, response);

        final GetSellerResponse seller = GetSellerResponse.from(post.getSeller());

        final int likesCount = postLikesRepository.countByPostId(postId);
        boolean isLiked = false;

        if(username != null) {
            final User loginUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
            isLiked = postLikesRepository.existsByUserIdAndPostId(loginUser.getId(), postId);
        }

        if (post instanceof Gifticon) {
            final GetGifticonDetailResponse getGifticonDetailResponse =
                    GetGifticonDetailResponse.from((Gifticon) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getGifticonDetailResponse);
        }
        else if (post instanceof Data) {
            final GetDataDetailResponse getDataDetailResponse =
                    GetDataDetailResponse.from((Data) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getDataDetailResponse);
        }
        else {
            throw new GeneralException(TradeException.POST_NOT_FOUND);
        }
    }

    public DeletePostResponse deletePost(final Long postId, final String username) {
        final User user = getUserByUsername(username);
        final Post post = validateAndGetPost(postId, username);

        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if(post.getIsSold()) {
            throw new GeneralException(TradeException.SOLD_POST_DELETE_DENIED);
        }

        post.updateIsDeleted();
        final PostDocument postDocument = PostDocument.from(post);
        postDocumentRepository.delete(postDocument);

        return DeletePostResponse.of(postId);
    }

    public SavePostResponse modifyPostData(final Long postId, final UpdateDataPostRequest updateDataPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        post.updateCommentAndPriceAndTitle(
                updateDataPostRequest.getComment(),
                updateDataPostRequest.getPrice(),
                updateDataPostRequest.getTitle()
        );

        final PostDocument postDocument = PostDocument.from(post);
        postDocumentRepository.save(postDocument);

        return SavePostResponse.of(post.getId());
    }

    public SavePostResponse modifyPostGifticon(final Long postId, final UpdateGifticonPostRequest updateGifticonPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        if (!(post instanceof Gifticon gifticon)) {
            throw new GeneralException(TradeException.GIFTICON_NOT_FOUND);
        }

        final double[] doubleVector = postVectorizerDouble.vectorizePost(
                updateGifticonPostRequest.getPrice(),
                gifticon.getDeadLine(),
                gifticon.getCategory(),
                gifticon.getPartner()
        );

        final float[] floatVector = postVectorizerFloat.vectorizePost(
                updateGifticonPostRequest.getPrice(),
                gifticon.getDeadLine(),
                gifticon.getCategory(),
                gifticon.getPartner()
        );

        post.updateCommentAndPrice(
                updateGifticonPostRequest.getComment(),
                updateGifticonPostRequest.getPrice()
        );

        gifticon.updateDoubleVector(doubleVector);
        gifticon.updateFloatVector(floatVector);
        jdbcRepository.updatePostVector(gifticon.getId(), floatVector);

        final PostDocument postDocument = PostDocument.from(gifticon);
        postDocumentRepository.save(postDocument);

        return SavePostResponse.of(post.getId());
    }

    public String generateGifticons() {
        List<GifticonCategory> categories = gifticonCategoryRepository.findAll();
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        List<LocalDate> dates = Stream.iterate(LocalDate.now().plusDays(0), date -> date.plusDays(2))
                .limit(60)
                .toList();

        List<BigDecimal> prices = new ArrayList<>();
        for (int price = 1000; price <= 30000; price += 1000) {
            prices.add(BigDecimal.valueOf(price));
        }

        int index = 1;

        for (GifticonCategory category : categories) {
            List<Partner> partners = partnerRepository.findByCategoryId(category.getId());

            for (Partner partner : partners) {
                for (LocalDate date : dates) {
                    for (BigDecimal price : prices) {
                        final double[] doubleVector = postVectorizerDouble.vectorizePost(
                                price,
                                date,
                                category,
                                partner.getPartner()
                        );

                        final float[] floatVector = postVectorizerFloat.vectorizePost(
                                price,
                                date,
                                category,
                                partner.getPartner()
                        );

                        Gifticon gifticon = new Gifticon(
                                user,
                                partner.getPartner() + " 싸게 팔아요",
                                "이 기프티콘은 " + partner.getPartner() + "에서 사용 가능합니다.",
                                price,
                                date,
                                "temp.png",
                                false,
                                index+"",
                                partner.getPartner(),
                                category,
                                doubleVector,
                                floatVector
                        );

                        gifticonRepository.save(gifticon);
                        jdbcRepository.insertPostVector(gifticon.getId(), floatVector);
                        log.info("post id : " + index);

                        index++;
                    }
                }
            }
        }

        return "success";
    }

    private Post validateAndGetPost(final Long postId, final String username) {
        final User user = getUserByUsername(username);
        final Post post = getPostById(postId);

        validatePostStatus(post);
        validatePostOwnership(post, user);

        return post;
    }

    private User getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));
    }

    private void validatePostStatus(final Post post) {
        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold()) {
            throw new GeneralException(TradeException.SOLD_POST_ALREADY);
        }
    }

    private void validatePostOwnership(final Post post, final User user) {
        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }
    }
}
