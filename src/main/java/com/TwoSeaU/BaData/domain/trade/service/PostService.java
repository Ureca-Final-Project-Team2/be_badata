package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.OCRResult;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.user.entity.SearchHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.SearchHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final GifticonRepository gifticonRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;
    private final PostLikesRepository postLikesRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final ELAService elaService;
    private final PostVectorizer postVectorizer;
    private final S3ImageService s3ImageService;
    private final OCRService ocrService;

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
    private void saveSearchHistoryIfUserExists(final String username, final String query) {
        if (username == null) {
            return;
        }

        try {
            final User user = userRepository.findByUsername(username).orElseThrow(() ->
                    new GeneralException(UserException.USER_NOT_FOUND));

            searchHistoryRepository.save(SearchHistory.of(user, query));
        } catch (Exception e) {
            log.info("검색 기록 저장에 실패: {}", e.getMessage());
        }
    }

    public CursorPageResponse<PostResponse> searchPosts(final String query, final String username, final Long cursor, final int size) {

        if (query != null && !query.isEmpty()) {
            log.info("event-keyword-search, {}", query);
            saveSearchHistoryIfUserExists(username, query);
        }

        return postRepository.searchPostsByKeyword(query, username, cursor, size);
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
        final double[] vector = postVectorizer.vectorizePost(
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                category
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
                vector
        );

        final Gifticon savedGifticon = gifticonRepository.save(gifticon);

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

        return SavePostResponse.builder()
                .postId(savedData.getId())
                .build();
    }

    public GetPostDetailResponse getPost(final Long postId, final String username) {
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

        return DeletePostResponse.of(postId);
    }

    public SavePostResponse modifyPostData(final Long postId, final UpdateDataPostRequest updateDataPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        post.updateCommentAndPriceAndTitle(
                updateDataPostRequest.getComment(),
                updateDataPostRequest.getPrice(),
                updateDataPostRequest.getTitle()
        );

        return SavePostResponse.of(post.getId());
    }

    public SavePostResponse modifyPostGifticon(final Long postId, final UpdateGifticonPostRequest updateGifticonPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        post.updateCommentAndPrice(
                updateGifticonPostRequest.getComment(),
                updateGifticonPostRequest.getPrice()
        );

        return SavePostResponse.of(post.getId());
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
            throw new GeneralException(TradeException.EXPIRED_POST_MODIFY);
        }
    }

    private void validatePostOwnership(final Post post, final User user) {
        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }
    }
}
